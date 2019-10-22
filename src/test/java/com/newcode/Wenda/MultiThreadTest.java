package com.newcode.Wenda;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ThreadTest extends Thread{
     private int id;
     private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();


     public static void threadLocalTest(){

         for (int i = 0; i <10; i++) {
             final int finalI = i;
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                    threadLocal.set(finalI);
                     try {
                         Thread.sleep(1000);
                         System.out.println("threadLocal:"+threadLocal.get());
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
             }).start();

         }
     }
     public ThreadTest(int id){
         this.id=id;
     }

     @Override
     public void run() {

         try {
             for (int i = 0; i <10;i++){
                 Thread.sleep(1000);
                 System.out.println(String.format("%d,%d",id,i));

             }
         }catch (Exception e){
             e.printStackTrace();
         }

     }
 }
public class MultiThreadTest {

    public static void testThread(){
        for (int i = 0; i < 10; i++) {
            ThreadTest threadTest =new ThreadTest(i);
            threadTest.start();
        }

    }
    public static void testExecutor(){
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10 ; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println(i);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    public static void main(String[] args) {
        //testThread();
        //ThreadTest.threadLocalTest();
        testExecutor();
    }


}
