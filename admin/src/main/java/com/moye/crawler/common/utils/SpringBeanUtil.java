package com.moye.crawler.common.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Spring 工具类
 *
 */
public class SpringBeanUtil implements ServletContextListener {

	private static WebApplicationContext springContext;

	public void contextInitialized(ServletContextEvent event) {
		springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent event) {

	}

	public static ApplicationContext getApplicationContext() {
		return springContext;
	}

	public SpringBeanUtil() {
	}
	
	
	public static <T> T getBean(Class<T> requiredType){
		
		if(springContext == null){
			
			throw new RuntimeException("springContext is null.");
		}
		return springContext.getBean(requiredType);
	}
	
}
