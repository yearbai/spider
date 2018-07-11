package com.moye.crawler.factory;

/**
 * @author: moye
 * @description:
 * @date Created in  2018/5/28 10:33
 * @modified By
 */
public class HairFactory {

    public HairInterface getHair(String className){
        try {
            HairInterface factory = (HairInterface) Class.forName( className ).newInstance();
            return factory;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
