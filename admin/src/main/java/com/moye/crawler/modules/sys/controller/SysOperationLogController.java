package com.moye.crawler.modules.sys.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.moye.crawler.base.annotion.BussinessLog;
import com.moye.crawler.common.utils.PageInfo;
import com.moye.crawler.common.utils.QueryCondition;
import com.moye.crawler.entity.sys.SysOperationLog;
import com.moye.crawler.modules.sys.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 操作日志 前端控制器
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Controller
@RequestMapping("/log")
public class SysOperationLogController {

    @Autowired
    private SysOperationLogService operationLogService;
    /**
     * 新的页面打开 curd demo
     */

//    @RequiresPermissions("/user/page/list")
    @RequestMapping(value = "/business/list", method = RequestMethod.GET )
    public String pagelist(String id, HttpServletRequest request) {
        System.out.println("------");
        request.setAttribute("selectId", id == null ? 0 : id);
        return "/base/log/business_log_list";
    }

    @BussinessLog(value = "用户操作历史列表")
    @ResponseBody
    @RequestMapping(value = "/business/loadData", produces = "application/json;charset=utf-8")
    public String loadData(String reqObj){
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<SysOperationLog> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());

        Page<SysOperationLog> userPage = operationLogService.selectPage(page, new EntityWrapper<>());
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }
}
