package com.moye.crawler.common.controller;

import org.springframework.stereotype.Controller;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/4 11:56
 * @Modified By
 */
@Controller
public class BaseController {
    protected static String SUCCESS = "SUCCESS";
    protected static String ERROR = "ERROR";

    protected static String REDIRECT = "redirect:";
    protected static String FORWARD = "forward:";
}
