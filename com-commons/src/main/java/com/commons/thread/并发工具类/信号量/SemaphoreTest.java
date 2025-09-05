package com.commons.thread.并发工具类.信号量;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
/**
 * 应用场景：共享资源的争夺，例如游戏中选手进入房间的情况。
 * @author hy
 *
 */
public class SemaphoreTest {  
    public static void main(String[] args) {  

    	//创建一个可根据需要创建新线程的线程池  
    	ExecutorService service = Executors.newCachedThreadPool();  
        final  Semaphore sp = new Semaphore(3);  
    
        //创建10个线程  
        for(int i=0;i<10;i++){  
            Runnable runnable = new Runnable(){  
                    public void run(){  
                    try {  
                        sp.acquire();   //获取灯，即许可权  
                    } catch (InterruptedException e1) {  
                        e1.printStackTrace();  
                    }  
                    System.out.println("线程" + Thread.currentThread().getName() +   
                            "进入，当前已有" + (3-sp.availablePermits()) + "个并发");  
                    try {  
                        Thread.sleep((long)(Math.random()*10000));  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                    System.out.println("线程" + Thread.currentThread().getName() +   
                            "即将离开");                      
                    sp.release();   // 释放一个许可，将其返回给信号量  

                    //下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元  
                    System.out.println("线程" + Thread.currentThread().getName() +   
                            "已离开，当前已有" + (3-sp.availablePermits()) + "个并发");                      
                }  
            };  
            service.execute(runnable);            
        }  
    }  
}  