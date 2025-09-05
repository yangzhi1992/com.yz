package com.commons.thread.书籍.并发编程实战;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Preloader
 *
 * Using FutureTask to preload data that is needed later
 *
 * @author Brian Goetz and Tim Peierls
 */

public class Preloader {
	public static void main(String[] args) throws DataLoadException, InterruptedException {
		final Preloader p = new Preloader();
		p.start();
		
		Thread t0  = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					p.get();
				} catch (DataLoadException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		t0.start();
		
		Thread t = new Thread(new FutureTask(new Callable(){
			@Override
			public Object call() throws Exception {
				System.out.println("1");
				return null;
			}
		}){
//			public void run() {
//				System.out.println("2");
//			}
		});
		
//		t.start();
	}
	
	Integer loadProductInfo() throws DataLoadException {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("before 2");
        return 2;
    }

    private final FutureTask<Integer> future = new FutureTask<Integer>(new Callable<Integer>() {
            public Integer call() throws DataLoadException {
                return loadProductInfo();
            }
    });
    
    private final Thread thread = new Thread(future);

    public void start() { thread.start(); }

    public Integer get() throws DataLoadException, InterruptedException {
        try {
        	System.out.println(future.get());
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof DataLoadException)
                throw (DataLoadException) cause;
            else
                throw LaunderThrowable.launderThrowable(cause);
        }
    }

    interface ProductInfo {
    }
}

class DataLoadException extends Exception { 
	
}
