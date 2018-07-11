//package com.moye.crawler.core.shiro.filters;
//
//import com.infinite.common.entity.SysUser;
//import com.infinite.common.shiro.ShiroManager;
//import org.apache.shiro.web.filter.AccessControlFilter;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 判断登录
// */
//public class LoginFilter  extends AccessControlFilter {
//	final static Class<LoginFilter> CLASS = LoginFilter.class;
//	@Override
//	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
//
//		SysUser token = ShiroManager.getSessionUser();
//
//		if(null != token || isLoginRequest(request, response)){// && isEnabled()
//            return Boolean.TRUE;
//        }
//		if (ShiroFilterUtils.isAjax(request)) {// ajax请求
//			Map<String,String> resultMap = new HashMap<String, String>();
//			//LoggerUtils.debug(getClass(), "当前用户没有登录，并且是Ajax请求！");
//			resultMap.put("login_status", "300");
//			resultMap.put("message", "当前用户没有登录");//当前用户没有登录！
//			ShiroFilterUtils.out(response, resultMap);
//		}
//		return Boolean.FALSE ;
//
//	}
//
//	@Override
//	protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
//			throws Exception {
//		//保存Request和Response 到登录后的链接
//		saveRequestAndRedirectToLogin(request, response);
//		return Boolean.FALSE ;
//	}
//
//
//}
