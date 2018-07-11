package com.moye.crawler.base.exception;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.NOT_EXTENDED;

/**
 * 进入方法后，拦截参数错误 运行时异常
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 在controller里面内容执行之前，校验一些参数不匹配啊，Get post方法不对啊之类的
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        System.out.println("错误");
        insertLog("记录在执行@RequestMapping时，进入逻辑处理阶段前错误日志。。。。。");
        ex.printStackTrace();
        return new ResponseEntity<Object>("出错了", NOT_EXTENDED);

    }

    /**
     * 所有异常报错
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public ModelAndView allExceptionHandler(HttpServletRequest request, Exception exception) throws Exception {
        exception.printStackTrace();
        System.out.println("我报错了：" + exception.getLocalizedMessage());
        System.out.println("我报错了：" + exception.getCause());
        System.out.println("我报错了：" + exception.getSuppressed());
        System.out.println("我报错了：" + exception.getMessage());
        System.out.println("我报错了：" + exception.getStackTrace());
        insertLog("记录在controller里执行逻辑代码时出的异常错误日志。。。。。");
        ModelAndView view = new ModelAndView("/505");
        if(exception instanceof UnauthorizedException){
            view.addObject("msg","没有权限");
            return view;
        }
        view.addObject("msg","服务器异常，请联系管理员！");
        return view;
    }

    private void insertLog(String log){
        System.out.println(log);
    }
}