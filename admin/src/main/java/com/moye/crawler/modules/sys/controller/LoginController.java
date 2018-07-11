package com.moye.crawler.modules.sys.controller;

import com.google.gson.Gson;
import com.moye.crawler.common.controller.BaseController;
import com.moye.crawler.common.utils.ResultCode;
import com.moye.crawler.core.shiro.ShiroManager;
import com.moye.crawler.entity.sys.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/4 11:54
 * @Modified By
 */
@Controller
public class LoginController extends BaseController {

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (ShiroManager.isAuthenticated() || ShiroManager.getSessionUser() != null) {
            return REDIRECT + "/";
        } else {
            return "/login";
        }
    }

    /**
     * 登陆
     * @param userName
     * @param password
     * @param captcha
     * @param rememberMe
     * @param request
     * @return
     */

    @RequestMapping(value = "/doLogin")
    public String doLogin(String userName, String password, String captcha, Boolean rememberMe, HttpServletRequest request, Model model) {
        System.out.println(request.getHeader("content-type"));
        String kaptcha = "";
      /*  try{
             kaptcha = ShiroManager.getKaptcha(Constants.KAPTCHA_SESSION_KEY);
        }catch (RRException e){
            map.put("msg", "验证码已失效");
            return "/login";
        }

        if(!captcha.equalsIgnoreCase(kaptcha)){
            map.put("msg", "验证码不正确");
        }
        */
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);

        if (currentUser.isAuthenticated()) {
            return REDIRECT + "/index";
        }else{
            // token.setRememberMe(true);
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                model.addAttribute("message", new ResultCode("1", "未知用户"));
                return "/login";
            } catch (IncorrectCredentialsException ice) {
                model.addAttribute("message", new ResultCode("1", "密码错误"));
                return "/login";
            } catch (LockedAccountException lae) {
                model.addAttribute("message", new ResultCode("1", "账号已锁定"));
                return "/login";
            } catch (AuthenticationException ae) {
                //unexpected condition?  error?
                model.addAttribute("message", new ResultCode("1", "服务器繁忙"));
                return "/login";
            }
        }
        /**
         * 记录登录日志
         */
        SysUser sysUser = ShiroManager.getSessionUser();
//        SysUser sysUser = (SysUser) currentUser.getSession().getAttribute("userSession");
       // logService.insertLog("用户登录成功", sysUser.getUserName(), request.getRequestURI(), "");
        System.out.println(new Gson().toJson(sysUser));
        return "redirect:/index";

    }

    /**
     * 注销调用此方法，需要注释request.setAttribute，因为会话删除后会出现问题，必须使用redirect:/login代替 LOGIN_PAGE
     * 还有可以使用SystemLogoutFilter进行重定向
     * 具体使用哪种方式，详见spring-shiro.xml的配置，本项目没使用SystemLogoutFilter
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/logout")
    private String doLogout(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login";
    }


}
