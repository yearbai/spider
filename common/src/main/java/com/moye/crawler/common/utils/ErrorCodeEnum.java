package com.moye.crawler.common.utils;

import org.apache.commons.lang3.StringUtils;

public enum ErrorCodeEnum {
    NOT_FOUND( "404", "404" ), SYSTEM_ERROR( "系统错误", "505" ), NOT_LOGIN( "未登陆", "0001" ), YELLO( "黄色", "" );
    // 成员变量  
    private String name;
    private String index;

    // 构造方法  
    private ErrorCodeEnum(String name, String code) {
        this.name = name;
        this.index = index;
    }

    // 普通方法  
    public static String getName(String index) {
        for (ErrorCodeEnum c : ErrorCodeEnum.values()) {
            if (StringUtils.equals( c.getIndex() , index )) {
                return c.name;
            }
        }
        return null;
    }

    // get set 方法  
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
} 