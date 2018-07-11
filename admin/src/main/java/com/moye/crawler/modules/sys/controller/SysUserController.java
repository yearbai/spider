package com.moye.crawler.modules.sys.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import com.moye.crawler.base.annotion.BussinessLog;
import com.moye.crawler.common.utils.PageInfo;
import com.moye.crawler.common.utils.QueryCondition;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.core.shiro.MyShiroRealm;
import com.moye.crawler.entity.sys.SysRole;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.SysUserRole;
import com.moye.crawler.modules.sys.service.SysRoleService;
import com.moye.crawler.modules.sys.service.SysUserRoleService;
import com.moye.crawler.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 前端控制器
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Controller
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysUserRoleService userRoleService;

    private final static String initPassword=  "123456";

    /**
     * 新的页面打开 curd demo
     */

    @RequiresPermissions("/user/page/list")
    @RequestMapping(value = "/page/list", method = RequestMethod.GET )
    public String pagelist(String id, HttpServletRequest request) {
        System.out.println("------");
        request.setAttribute("selectId", id == null ? 0 : id);
        return "/base/user/user_page_list";
    }

    @BussinessLog(value = "查询用户列表")
    @ResponseBody
    @RequestMapping(value = "/loadData", produces = "application/json;charset=utf-8")
    public String loadData(String reqObj){
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<SysUser> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        Map<String, String> paramMap = queryCondition.getConditionMap();
        EntityWrapper<SysUser> wrapper = new EntityWrapper<>();
        paramMap.entrySet().forEach(entry ->{
            if(StringUtils.isNotBlank(entry.getValue())){
                wrapper.eq(entry.getKey(),entry.getValue());
            }
            System.out.println("key:value = " + entry.getKey() + ":" + entry.getValue());
        });
        Page<SysUser> userPage = userService.selectPage(page, wrapper);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }



    /**
     * 用户编辑 new page
     *
     * @return
     */
//    @RefreshCSRFToken
    @RequiresPermissions("/user/page/edit")
    @RequestMapping(value = "/page/edit", method = RequestMethod.GET)
    public String pageEdit(String id, HttpServletRequest request, Model model) {
        request.setAttribute("id", id == null ? 0 : id);
        EntityWrapper<SysRole> wrapper = new EntityWrapper<>();
        wrapper.eq("status", "0");
        SysRole role = new SysRole();
        role.setStatus(0);
        List<SysRole> roleList = roleService.selectList(wrapper);
        model.addAttribute("roleList",roleList);
        return "/base/user/user_page_edit";
    }


    @RequestMapping(value = "/get", method = RequestMethod.POST )
    @ResponseBody
    private SysUser getUser(String id) {
        System.out.println(id);
        System.out.println(JSONObject.toJSON(userService.selectById(id)));
        SysUser user =  userService.selectById(id);
        return user;
    }


//    @VerifyCSRFToken
    @RequestMapping(value = "/save",method = RequestMethod.POST )
    @ResponseBody
    public Result saveUser(SysUser user, HttpServletRequest request) {
        try{
            if (user.getId() == null) {
                //设置初始密码
                user.setPassword(MyShiroRealm.getPassword(initPassword,user.getAccount()));
                userService.insert(user);
//            String userId = userService.save(user).toString();
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleid(user.getRoleid());
                userRole.setUserid(user.getId());
                userRoleService.insert(userRole);
                //头像和用户管理
                //userService.updateUserAvatar(user, request.getRealPath("/"));
            } else {
                SysUser oldUser=this.getUser(user.getId().toString());
                BeanUtils.copyProperties(user,oldUser,"password");
                if(!oldUser.getAccount().equals(user.getAccount())){
                    oldUser.setPassword(MyShiroRealm.getPassword(initPassword,user.getAccount()));
                }
                //oldUser.setUpdateDateTime(new Date());
                userService.updateById(oldUser);
                SysUserRole userRole = new SysUserRole();
                userRole.setRoleid(user.getRoleid());
                userRole.setUserid(user.getId());
                EntityWrapper<SysUserRole> wrapper = new EntityWrapper<>();
                wrapper.eq("userid", user.getId());
                userRoleService.update(userRole, wrapper);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(true);
    }

    @RequestMapping( value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    private Result deleteUser(@PathVariable("id") String id) {

        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            return new Result(false);
        }
        return new Result(true);
    }


    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("key","value");
        map.put("key1","value1");
        map.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + "---" + entry.getValue());
        });
    }
}
