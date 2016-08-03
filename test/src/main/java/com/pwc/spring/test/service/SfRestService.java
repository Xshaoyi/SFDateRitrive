package com.pwc.spring.test.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.pwc.spring.test.model.GeoWrapper;
import com.pwc.spring.test.thread.SingleThreadExecutingService;
import com.pwc.spring.test.thread.TaskResult;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class SfRestService {
	private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	private static final String INSTANCE_URL = "INSTANCE_URL";

	private String USERNAME;
	private String PASSWORD;
	private String LOGINURL;
	private String GRANTSERVICE;
	private String CLIENTID;
	private String CLIENTSECRET;

	private String accessToken;
	private String instanceUrl;
	private String loginAccessToken = null;
	private String loginInstanceUrl = null;
	LinkedBlockingQueue<GeoWrapper> geoWrappers= new LinkedBlockingQueue<GeoWrapper>(1000);
	public void GetOauthToken() {
		HttpClient httpclient = HttpClientBuilder.create().build();

		// Assemble the login request URL
		String loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET
				+ "&username=" + USERNAME + "&password=" + PASSWORD;

		// Login requests must be POSTs
		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse response = null;
		try {
			// Execute the login POST request
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException cpException) {
			cpException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		// verify response is HTTP OK
		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			System.out.println("Error authenticating to Force.com: " + statusCode);
			// Error is in EntityUtils.toString(response.getEntity())
			return;
		}

		String getResult = null;
		try {
			getResult = EntityUtils.toString(response.getEntity());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		JSONObject jsonObject = null;

		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			loginAccessToken = jsonObject.getString("access_token");
			loginInstanceUrl = jsonObject.getString("instance_url");
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
		System.out.println(response.getStatusLine());
		System.out.println("Successful login");
		System.out.println("  instance URL: " + loginInstanceUrl);
		System.out.println("  access token/session ID: " + loginAccessToken);

		// release connection
		httpPost.releaseConnection();
	}

	public void testGet() throws URISyntaxException {
		HttpClient httpclient = HttpClientBuilder.create().build();
		String url = loginInstanceUrl + "/services/data/v37.0/query";
		URI uri = new URIBuilder(url).addParameter("q", "select id,name from lead").build();
		HttpGet get = new HttpGet(uri);
		get.addHeader("Content-Type", "application/json");
		get.addHeader("Authorization", "Bearer " + loginAccessToken);
		HttpResponse response = null;
		try {
			response = httpclient.execute(get);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String getResult = null;

		try {
			getResult = EntityUtils.toString(response.getEntity());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		System.out.println(getResult);
		JSONObject jsonObject = null;
		jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
		Boolean isDone = jsonObject.getBoolean("done");
		String nextQuery = jsonObject.getString("nextRecordsUrl");
//		JSONArray recordArray = jsonObject.getJSONArray("records");
//		for (Object o : recordArray) {
//			JSONObject jo = (JSONObject) o;
//			System.out.println(jo.toString());
//		}
		

	}

	public void getDataFromSalesforce() throws URISyntaxException {
		final HttpClient httpclient = HttpClientBuilder.create().build();
		SingleThreadExecutingService<TaskResult> es = new SingleThreadExecutingService<TaskResult>();
		String path = "/services/data/v37.0/query";
		while (true) {
			TaskResult tr =es.submitTask(new Task(path,httpclient));
			if(tr.getIsDone()){
				break;
			}else{
				path = tr.getNextQuery();
				System.out.println(path);
			}
		}

		// System.out.println(getResult);

	}
	JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.186.128");
	public void uploadDataToRedis(){
		ExecutorService ex = Executors.newFixedThreadPool(1);
		while(true){
			ex.submit(new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					try  {
						Jedis jedis = pool.getResource();
						GeoWrapper geo=geoWrappers.take();
						jedis.geoadd("samsgeo", geo.getGeoCoordinate().getLongitude(), geo.getGeoCoordinate().getLatitude(), geo.getName());
						System.out.println("upload success name:"+geo.getName());
						jedis.close();
					}catch(Exception e){
						e.printStackTrace();			}
				}
			});
		}
	}
	private class Task<T> implements Callable<T>{
		private String path ;
		private HttpClient httpclient;
		public Task (String path,HttpClient httpclient){
			this.path = path;
			this.httpclient = httpclient;
		}
		public T call() throws Exception {
			// TODO Auto-generated method stub
			String url = loginInstanceUrl + path;
			URI uri = new URIBuilder(url).addParameter("q", "select id,name,Lead_Geo_Location__Latitude__s,Lead_Geo_Location__Longitude__s from lead where Lead_Geo_Location__Latitude__s!=null").build();
			HttpGet get = new HttpGet(uri);
			get.addHeader("Content-Type", "application/json");
			get.addHeader("Authorization", "Bearer " + loginAccessToken);
			HttpResponse response = null;
			try {
				response = httpclient.execute(get);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String getResult = null;

			try {
				getResult = EntityUtils.toString(response.getEntity());
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
			JSONObject jsonObject = null;
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			Boolean isDone = jsonObject.getBoolean("done");
			String nextQuery = jsonObject.getString("nextRecordsUrl");
			TaskResult tr = new TaskResult(isDone, nextQuery);
			JSONArray recordArray = jsonObject.getJSONArray("records");
			System.out.println(recordArray.toString());
			for (Object o : recordArray) {
				JSONObject jo = (JSONObject) o;
				GeoWrapper gewGeoWrapper = new GeoWrapper(jo.getString("Name"),
						jo.getDouble("Lead_Geo_Location__Longitude__s"),
						jo.getDouble("Lead_Geo_Location__Longitude__s"));
				geoWrappers.put(gewGeoWrapper);
			}
			return (T) tr;
		}
		
	}
	public String getUSERNAME() {
		return USERNAME;
	}

	public void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public String getLOGINURL() {
		return LOGINURL;
	}

	public void setLOGINURL(String lOGINURL) {
		LOGINURL = lOGINURL;
	}

	public String getGRANTSERVICE() {
		return GRANTSERVICE;
	}

	public void setGRANTSERVICE(String gRANTSERVICE) {
		GRANTSERVICE = gRANTSERVICE;
	}

	public String getCLIENTID() {
		return CLIENTID;
	}

	public void setCLIENTID(String cLIENTID) {
		CLIENTID = cLIENTID;
	}

	public String getCLIENTSECRET() {
		return CLIENTSECRET;
	}

	public void setCLIENTSECRET(String cLIENTSECRET) {
		CLIENTSECRET = cLIENTSECRET;
	}
}
