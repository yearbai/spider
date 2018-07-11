package com.moye.crawler.modules.sys.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.moye.crawler.common.utils.PageInfo;
import com.moye.crawler.common.utils.QueryCondition;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.entity.sys.SysRole;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.modules.sys.service.SysRoleService;
import com.moye.crawler.modules.sys.service.SysUserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Controller
@RequestMapping("/role")
public class SysRoleController {

    @Resource
    private SysRoleService roleService;

    @Autowired
    private SysUserService userService;

    /**
     * 角色列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private String list() {
        return "/base/auth/role_list";
    }

    @ResponseBody
    @RequestMapping(value = "/loadData", produces = "application/json;charset=utf-8")
    public String loadData(String reqObj) {
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<SysRole> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        Map<String, String> paramMap = queryCondition.getConditionMap();
        EntityWrapper<SysRole> wrapper = new EntityWrapper<>();
        paramMap.entrySet().forEach(entry -> {
            if (StringUtils.isNotBlank(entry.getValue())) {
                wrapper.eq(entry.getKey(), entry.getValue());
            }
            System.out.println("key:value = " + entry.getKey() + ":" + entry.getValue());
        });
        Page<SysRole> userPage = roleService.selectPage(page, wrapper);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/user/loadData", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String loadRoleUserData(String reqObj) {
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<SysUser> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        Map<String, Object> paramMap = queryCondition.getConditionMap();

        Page<SysUser> userPage = userService.queryRoleUserList(page, paramMap);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }


    /**
     * 角色编辑
     *
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    private String eidt(String id, HttpServletRequest request) {

        request.setAttribute("id", id == null ? "0" : id);
        return "/base/auth/role_edit";
    }


    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Result save(SysRole role) {
        roleService.saveOrUpdate(role);
        return new Result(true);
    }

    @ResponseBody
    @RequestMapping(value = "/get", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public SysRole get(String id) {
        SysRole role = roleService.selectById(id);
        return role;
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Result delete(@PathVariable("id") String id) {
        if (StringUtils.isNotBlank(id)) {
            roleService.deleteById(NumberUtils.toInt(id));
            return new Result(true);
        } else {
            return new Result(false);
        }
    }

    /**
     * @param fieldName
     * @param fieldValue
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/checkUnique")
    public String checkUnique(String fieldName, String fieldValue, String id) {
        Map<String, Object> map = new HashMap<>();
        try {
            EntityWrapper<SysRole> wrapper = new EntityWrapper<>();
            wrapper.eq(fieldName, fieldValue);
            if (StringUtils.isNotBlank(id)) {
                wrapper.ne("id", id);
            }
            List<SysRole> role = roleService.selectList(wrapper);
            map.put("valid", role.isEmpty());
        } catch (Exception e) {
            map.put("valid", false);
        }
        return JSONObject.toJSONString(map);
    }

    public static void main(String[] args) {
        String str = "";
        System.out.println(StringUtils.isNotBlank(str));
    }
}
