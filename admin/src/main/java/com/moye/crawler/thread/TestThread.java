package com.moye.crawler.thread;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/28 10:47
 * @modified By
 */
public class TestThread {

    public static void main(String[] args) {
        TestThread main = new TestThread();
        main.startThread();
    }

    /**
     * 线程锁
     */
    private final Object object = new Object();

    /**
     * 启动线程
     */
    public void startThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("开始执行线程。。。");
                System.out.println("进入等待状态。。。");
                synchronized (object) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("线程结束。。。");
            }
        });
        t.start();
    }
}
