package com.moye.crawler.modules.sys.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.entity.sys.SysRoleMenu;
import com.moye.crawler.modules.sys.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 角色和菜单关联表 前端控制器
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Controller
@RequestMapping("/rolemenu")
public class SysRoleMenuController {

    @Autowired
    private SysRoleMenuService roleMenuService;

    //角色授权管理主界面
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private String list() {

        return "/base/auth/rolefunc_list";
    }


    //跳转到授权页面 restful风格
    @RequestMapping(value = "/select/{roleId}", method = RequestMethod.GET)
    private String select(@PathVariable("roleId") String roleId, HttpServletRequest request) {
        request.setAttribute("roleId", roleId);
        return "/base/auth/rolefunc_select";
    }



    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    private Result save(SysRoleMenu rfobj) {
        roleMenuService.insert(rfobj);
        return new Result(true);
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    private SysRoleMenu get(String roleId, String functionId) {
        EntityWrapper<SysRoleMenu> wrapper = new EntityWrapper<>();
        wrapper.eq("roleid",roleId).eq("menuid", functionId);
        return roleMenuService.selectOne(wrapper);
    }



//
    /**
     * 批量保存角色授权（角色绑定功能）
     *
     * @param rflist 角色绑定功能列表
     * @return
     */
    @RequestMapping(value = "/save_batch", method = RequestMethod.POST)
    @ResponseBody
    public Result saveBatch(String roleId, String rflist) {
        List<SysRoleMenu> roleFunctionList = JSON.parseArray(rflist, SysRoleMenu.class);
        return roleMenuService.saveBatchRoleMenu(roleId, roleFunctionList);
    }


}
