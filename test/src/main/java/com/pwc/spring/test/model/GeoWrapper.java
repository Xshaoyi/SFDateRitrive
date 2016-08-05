package com.pwc.spring.test.model;

import redis.clients.jedis.GeoCoordinate;

public class GeoWrapper {
	private String name;
	public GeoWrapper(String name, double longtitude,double latitude) {
		super();
		this.name = name;
		this.geoCoordinate = new GeoCoordinate(longtitude,latitude);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public GeoCoordinate getGeoCoordinate() {
		return geoCoordinate;
	}
	public void setGeoCoordinate(GeoCoordinate geoCoordinate) {
		this.geoCoordinate = geoCoordinate;
	}
	private GeoCoordinate geoCoordinate;
	
	
}
