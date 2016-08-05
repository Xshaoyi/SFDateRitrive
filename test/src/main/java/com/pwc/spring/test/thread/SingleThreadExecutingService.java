package com.pwc.spring.test.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SingleThreadExecutingService<T> {
	ExecutorService ec = Executors.newSingleThreadExecutor();
	public T submitTask (Callable<T> call){
		Future<T> ft=ec.submit(call);
		T result = null;
		try {
			result = ft.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return result;
		}
	}

}
