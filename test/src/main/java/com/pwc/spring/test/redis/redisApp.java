package com.pwc.spring.test.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Jedis;

public class redisApp {
	public static void main(String[] agrs){
		
//		JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.186.128");
//		try  {
//				Jedis jedis = pool.getResource();
//			  /// ... do stuff here ... for example
//			  jedis.set("foo", "bar");
//			  String foobar = jedis.get("foo");
//			  jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike"); 
//			  Set<String> sose = jedis.zrange("sose", 0, -1);
//			}catch(Exception e){
//				e.printStackTrace();			}
//			/// ... when closing your application:
//			pool.destroy();
		Jedis jedis = new Jedis("192.168.186.128", 6379);
//		jedis.set("ni", "hao");
		String key = "nihao";
		Map<String, GeoCoordinate> map = new  HashMap<String, GeoCoordinate>();
		map.put("a", new GeoCoordinate(-83.342713,42.670424));
//		jedis.geoadd
//		jedis.close();
	}
}
