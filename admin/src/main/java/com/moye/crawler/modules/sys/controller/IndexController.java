package com.moye.crawler.modules.sys.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.moye.crawler.core.shiro.ShiroManager;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.vo.TreeMenu;
import com.moye.crawler.modules.sys.service.SysMenuService;
import org.elasticsearch.index.mapper.SourceToParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: moye
 * @Description:
 * @Date Created in  2018/4/4 16:29
 * @Modified By
 */
@Controller
public class IndexController {
    @Autowired
    private SysMenuService sysMenuService;



    @RequestMapping(value = "/index")
    public String index(Model model){
        SysUser user = (SysUser) ShiroManager.getSessionAttribute("user");
        model.addAttribute("user",user);
        return "/main";
    }

    @ResponseBody
    @RequestMapping(value = "/getMenuList", produces = "application/json;charset=utf-8")
    public String getMenuList(){

        List<TreeMenu> menuList =sysMenuService.selectTreeMenuByUserId(ShiroManager.getSessionUid().toString());
        System.out.println(com.alibaba.fastjson.JSONObject.toJSON(menuList));
        return JSONObject.toJSONString(menuList);
    }

    @RequestMapping(value = "/homePage", method = RequestMethod.GET )
    public String homePage(HttpServletRequest request) {
        return "/homePage";
    }
}
