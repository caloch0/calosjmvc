package org.caloch.utils;

import java.util.concurrent.ConcurrentHashMap;

class TConcurrent {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
        ThreadLocal<String> l=new ThreadLocal<>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    l.set("t1");
                    Thread.sleep(1000);
                    String name = Thread.currentThread().getName();
                    System.out.println(name);
                    map.put(name,2);
                    System.out.println(l.get());
                    System.out.println(map.get("t3"));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        },"t3");
        t1.start();
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                l.set("t2");
                String name = Thread.currentThread().getName();
                System.out.println(name);
                map.put("t3",4);
                System.out.println(l.get());
                System.out.println(map.get("t3"));
            }
        },"t2");
        t2.start();
        System.out.println(Thread.currentThread().getName());

    }
}