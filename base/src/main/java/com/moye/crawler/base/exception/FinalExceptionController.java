package com.moye.crawler.base.exception;


import com.google.gson.Gson;
import com.moye.crawler.common.utils.JsonResult;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

//import org.springframework.boot.web.servlet.error.ErrorController;

/**
 * @Author: moye
 * @Description: 未进入方法前，拦截404 500
 * @Date Created in  2018/3/12 11:02
 * @Modified By
 */
@Controller
public class FinalExceptionController implements ErrorController {

    private static final String ERROR_PATH = "/error";
    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @RequestMapping(value = ERROR_PATH)
    public String handlerError() {
        System.out.println("------------error 404--------");
        return "error/404";
    }

    @ExceptionHandler(value = UnauthorizedException.class)//处理访问方法时权限不足问题
    @ResponseBody
    public String defaultErrorHandler(HttpServletRequest req, Exception e) {
        System.out.println(e.getMessage());
        JsonResult result = new JsonResult();
        result.setSuccess(false);
        result.setStatus("500");
        result.setMsg("无权限访问");
        return new Gson().toJson(result);
    }
}
