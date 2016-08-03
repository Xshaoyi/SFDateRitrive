package com.pwc.spring.test;

import java.net.URISyntaxException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pwc.spring.test.model.GeoWrapper;
import com.pwc.spring.test.service.FirstService;
import com.pwc.spring.test.service.SfRestService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext context = new ClassPathXmlApplicationContext("/config.xml");
        FirstService fs=context.getBean("1stService",FirstService.class);
        System.out.println(fs.getUserList());
        final SfRestService sfR=context.getBean("sfRestService",SfRestService.class);
        sfR.GetOauthToken();
    	new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					sfR.getDataFromSalesforce();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
    	sfR.uploadDataToRedis();
//    	new Thread(new Runnable() {
//
//			public void run() {
//				// TODO Auto-generated method stub
//				sfR.uploadDataToRedis();
//			}
//    		
//    	}).start();;
		
    }
}
