package com.moye.crawler.modules.sys.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;

import com.moye.crawler.common.utils.PageInfo;
import com.moye.crawler.common.utils.QueryCondition;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.SysUserRole;
import com.moye.crawler.modules.sys.service.SysUserRoleService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/userrole")
public class SysUserRoleController {


    @Resource
    private SysUserRoleService userRoleService;


    /**
     * 用户选择
     *
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.GET)
    private String select(String roleId, HttpServletRequest request) {

        request.setAttribute("roleId", roleId);
        return "/base/auth/userrole_select";
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public SysUserRole get(String id) {

        SysUserRole ur = userRoleService.selectById(NumberUtils.toInt(id));
        return ur;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(String urlist) {

        List<SysUserRole> urs = JSON.parseArray(urlist, SysUserRole.class);
//        for (SysUserRole ur : urs) {
        //清除redis缓存
//           userRoleService.deleteAuthInRedis(ur.getUser().getId());
//        }
        System.out.println(JSONObject.toJSONString(urs));
        userRoleService.batchSave(urs);
        return new Result(true);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(String ids,String roleId) {
        try {
            userRoleService.deleteByIds(ids, roleId);
            return new Result(true);
        } catch (Exception e) {
            return new Result(false, "已经被其他数据引用，不可删除");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/unSelected/loadData" , produces = "application/json;charset=utf-8")
    public String unSelectedList(String reqObj){
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
        Page<SysUser> userPage = userRoleService.findUnSelectedUserList(page, paramMap);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }

    @ResponseBody
    @RequestMapping(value = "/selected/loadData" , produces = "application/json;charset=utf-8")
    public String selectedList(String reqObj){
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
        Page<SysUser> userPage = userRoleService.findSelectedUserList(page, paramMap);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }

}
