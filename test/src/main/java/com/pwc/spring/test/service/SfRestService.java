package com.pwc.spring.test.service;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SfRestService {
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String INSTANCE_URL = "INSTANCE_URL";

	private String USERNAME ;    
    private String PASSWORD ;
    private String LOGINURL     ;
    private String GRANTSERVICE ;
    private String CLIENTID    ;
    private String CLIENTSECRET;
    
	private String accessToken;
	private String instanceUrl;
	
	public void GetOauthToken(){
		HttpClient httpclient = HttpClientBuilder.create().build();
		 
        // Assemble the login request URL
        String loginURL = LOGINURL +
                          GRANTSERVICE +
                          "&client_id=" + CLIENTID +
                          "&client_secret=" + CLIENTSECRET +
                          "&username=" + USERNAME +
                          "&password=" + PASSWORD;
 
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
            System.out.println("Error authenticating to Force.com: "+statusCode);
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
        String loginAccessToken = null;
        String loginInstanceUrl = null;
        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginAccessToken = jsonObject.getString("access_token");
            loginInstanceUrl = jsonObject.getString("instance_url");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        System.out.println(response.getStatusLine());
        System.out.println("Successful login");
        System.out.println("  instance URL: "+loginInstanceUrl);
        System.out.println("  access token/session ID: "+loginAccessToken);
 
        // release connection
        httpPost.releaseConnection();
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