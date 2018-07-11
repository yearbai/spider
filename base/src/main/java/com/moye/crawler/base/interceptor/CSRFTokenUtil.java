package com.moye.crawler.base.interceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class CSRFTokenUtil {

    public static String generate(HttpServletRequest request) {

        return UUID.randomUUID().toString();
    }

}
