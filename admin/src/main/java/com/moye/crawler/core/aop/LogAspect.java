package com.moye.crawler.core.aop;


import com.moye.crawler.base.annotion.BussinessLog;
import com.moye.crawler.core.shiro.ShiroManager;
import com.moye.crawler.entity.sys.SysOperationLog;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.modules.sys.service.SysOperationLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/18 22:42
 * @Modified By
 */
@Aspect
@Component
public class LogAspect {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SysOperationLogService logService;

    //定义切面
    @Pointcut(value = "@annotation(com.moye.crawler.base.annotion.BussinessLog)")
    public void custService(){}

    @Around("custService()")
    public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {
        //先执行业务
        Object result = point.proceed();
        try {
            handle(point);
        } catch (Exception e) {
            log.error("日志记录出错!", e);
        }
        return result;
    }

    private void handle(ProceedingJoinPoint point) throws Exception {

        //获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();

        //如果当前用户未登录，不做日志
        SysUser user = ShiroManager.getSessionUser();
        if (null == user) {
            return;
        }

        //获取拦截方法的参数
        String className = point.getTarget().getClass().getName();
        Object[] params = point.getArgs();

        //获取操作名称
        BussinessLog annotation = currentMethod.getAnnotation(BussinessLog.class);
        String bussinessName = annotation.value();
        String key = annotation.key();
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
            sb.append(" & ");
        }
        SysOperationLog optLog = new SysOperationLog();
        optLog.setUserid(user.getId());
        optLog.setLogname(user.getName());
        optLog.setClassname(className);
        optLog.setLogtype(key);
        optLog.setOptname(bussinessName);
        optLog.setMethod(methodName);
        optLog.setParams(sb.toString());
        optLog.setCreatetime(new Date());
        logService.insert(optLog);
    }


}
