package com.moye.crawler.core.shiro.filters;

import com.moye.crawler.entity.sys.SysUser;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FormAuthFilter extends FormAuthenticationFilter
{
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
    {

        if (isLoginRequest(request, response))
        {
            if (isLoginSubmission(request, response))
            {
                //本次用户登陆账号
//                this.setPasswordParam("account");
//                SysUser newUser = getUserFromJson(request);
//                System.out.println(newUser.getAccount());
                String account = request.getParameter("userName");//newUser == null ? "" : newUser.getAccount();//;
                Subject subject = this.getSubject(request, response);
                //之前登陆的用户
                SysUser user = (SysUser) subject.getPrincipal();
                //如果两次登陆的用户不一样，则先退出之前登陆的用户
                if (account != null && user != null && !account.equals(user.getAccount()))
                {
                    subject.logout();
                }
            }
        }

        return super.isAccessAllowed(request, response, mappedValue);
    }


}