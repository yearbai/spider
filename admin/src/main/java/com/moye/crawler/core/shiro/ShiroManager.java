package com.moye.crawler.core.shiro;


import com.moye.crawler.entity.sys.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

/**
 * Created by moye on 2017/11/20.
 */
public class ShiroManager {

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 已认证通过的用户。不包含已记住的用户，这是与user标签的区别所在。与notAuthenticated搭配使用
     *
     * @return 通过身份验证：true，否则false
     */
    public static boolean isAuthenticated() {
        return getSubject() != null && getSubject().isAuthenticated();
    }


    /**
     * 获取当前Session中的用户
     * @return
     */
    public static SysUser getSessionUser(){

        Subject subject = SecurityUtils.getSubject();
        if(subject != null){
            Object object = subject.getPrincipal();
            if(object != null){
                SysUser sysUser = (SysUser) object;
                return sysUser;
            }
        }
        return null;
    }

    /**
     * 获取当前用户ID
     * @return
     */
    public static Integer getSessionUid(){

        SysUser sysUser = getSessionUser();

        if(sysUser != null){

            return sysUser.getId();
        }

        return null;
    }

    public static void setSessionAttribute(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getSessionAttribute(Object key) {
        return getSession().getAttribute(key);
    }

    public static boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }

    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

    public static String getKaptcha(String key) {
        Object kaptcha = getSessionAttribute(key);
        if(kaptcha == null){
           // throw new RRException("验证码已失效");
        }
        getSession().removeAttribute(key);
        return kaptcha.toString();
    }
}
