package com.pwc.spring.test.thread;

import java.util.concurrent.Callable;

public class Task<V> implements Callable<V> {
	private String path;
	
	public Task(String path) {
		super();
		this.path = path;
	}

	public V call() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
