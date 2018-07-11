package com.moye.crawler.factory;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/28 10:36
 * @modified By
 */
public class TestFactroy {
    public static void main(String[] args) {
        HairFactory factory = new HairFactory();
        HairInterface hari = factory.getHair( "com.moye.crawler.factory.LeftHair" );
        hari.drow();
    }
}
