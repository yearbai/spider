package com.moye.crawler.modules.sys.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;

import com.moye.crawler.common.utils.Result;
import com.moye.crawler.common.utils.TreeNode;
import com.moye.crawler.entity.sys.SysMenu;
import com.moye.crawler.entity.sys.SysRoleMenu;
import com.moye.crawler.modules.sys.service.SysMenuService;
import com.moye.crawler.modules.sys.service.SysRoleMenuService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Controller
@RequestMapping("/menu")
public class SysMenuController {


    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysRoleMenuService roleMenuService;
    /**
     * 菜单列表
     */
    @RequestMapping( value = "/tree", method = RequestMethod.GET)
    private String list() {
        return "/base/auth/function_tree";
    }

    /**
     * getTreeData 构造bootstrap-treeview格式数据
     *
     * @return
     */
    @RequestMapping(value = "/treeData", method = RequestMethod.POST)
    @ResponseBody
    public List<TreeNode> getTreeData() {
        return menuService.getTreeData();
    }


    @RequestMapping(value = "/list/{roleId}", method = RequestMethod.POST)
    @ResponseBody
    public List<String> getFunctionIdsByRoleItd(@PathVariable("roleId") String roleId) {
        EntityWrapper<SysRoleMenu> wrapper = new EntityWrapper<>();
        wrapper.eq("roleid", roleId);
        List<SysRoleMenu> roleMenuList = roleMenuService.selectList(wrapper);
        List<String> list = new ArrayList<>();
        roleMenuList.forEach(rolemenu -> list.add(rolemenu.getMenuid() + ""));
        return list;
    }


    @RequestMapping(value = "/listTree/{roleId}", method = RequestMethod.POST)
    @ResponseBody
    public List<TreeNode> getTreeDataByRoleId(@PathVariable("roleId") String roleId) {
        return menuService.getTreeDataByRoleId(roleId);
    }


    @RequestMapping(value = "/get/{id}", method = RequestMethod.POST)
    @ResponseBody
    public SysMenu get(@PathVariable("id") String id) {

        SysMenu function = menuService.selectById(id);
        if (function.getPid() != 0) {
            function.setParentName(menuService.selectById(function.getPid()).getName());
        } else {
            function.setParentName("系统菜单");
        }
        return function;
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(SysMenu function) {
//        function.setUpdateDateTime(new Date());
        menuService.saveOrUpdate(function);
        return new Result(true);
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
            EntityWrapper<SysMenu> wrapper = new EntityWrapper<>();
            wrapper.eq(fieldName, fieldValue);
            if (StringUtils.isNotBlank(id)) {
                wrapper.ne("id", id);
            }
            List<SysMenu> role = menuService.selectList(wrapper);
            System.out.println(role.isEmpty());
            map.put("valid",role.isEmpty());
        } catch (Exception e) {
            map.put("valid", false);
        }
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@PathVariable("id") String id) {

        try {
//            SysMenu menu = menuService.selectById(id);
            boolean flag = menuService.deleteMenu(NumberUtils.toInt(id));
            if(flag){
                return new Result(true);
            }else{
                return new Result(false, "该菜单/功能已经被其他数据引用，不可删除");
            }
        } catch (Exception e) {
            return new Result(false, "该菜单/功能已经被其他数据引用，不可删除");
        }
    }

}
