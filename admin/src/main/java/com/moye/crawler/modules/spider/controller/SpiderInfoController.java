package com.moye.crawler.modules.spider.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.moye.crawler.common.utils.PageInfo;
import com.moye.crawler.common.utils.QueryCondition;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.core.spider.common.Webpage;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.modules.spider.service.SpiderInfoService;
import com.moye.crawler.modules.spider.service.SpiderTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
@Controller
@RequestMapping("/spider")
public class SpiderInfoController {

    @Autowired
    private SpiderInfoService spiderInfoService;

    @Autowired
    private SpiderTaskService spiderTaskService;



    @RequestMapping(value = "/infoList")
    public String spiderInfo(String id, HttpServletRequest request){
        request.setAttribute("selectId", id == null ? 0 : id);
        return "/base/spider/spider_info_list";
    }


    @ResponseBody
    @RequestMapping(value = "/info/loadData", produces = "application/json;charset=utf-8")
    public String loadData(String reqObj){
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<SpiderInfo> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        Map<String, String> paramMap = queryCondition.getConditionMap();
        Page<SpiderInfo> userPage = spiderInfoService.findSipderInfoList(page, paramMap);
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
//    @RequiresPermissions("/info/edit")
    @RequestMapping(value = "/info/edit", method = RequestMethod.GET)
    public String pageEdit(String id, HttpServletRequest request, Model model) {
        request.setAttribute("id", id == null ? 0 : id);
        return "/base/spider/spider_info_edit";
    }


    @RequestMapping(value = "/info/get", method = RequestMethod.POST )
    @ResponseBody
    private SpiderInfo getUser(String id) {
        SpiderInfo spiderInfo =  spiderInfoService.selectById(id);
        return spiderInfo;
    }


    //    @VerifyCSRFToken
    @RequestMapping(value = "/info/save",method = RequestMethod.POST )
    @ResponseBody
    public Result saveSpiderInfo(SpiderInfo spiderInfo, HttpServletRequest request) {
        try{
            if (spiderInfo.getId() == null) {
                spiderInfo.setCreateTime(new Date());
                spiderInfoService.insert(spiderInfo);
            } else {
                spiderInfoService.updateById(spiderInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Result(true);
    }

    @ResponseBody
    @RequestMapping(value = "/info/start/{id}", produces = "application/json;charset=utf-8")
    public String start(@PathVariable("id") String id){
        Map<String, String> map = new HashMap<>();
        SpiderInfo info = spiderInfoService.selectById(id);
        if(info == null){
            return JSONObject.toJSONString(new Result(false));
        }
        String taskId = spiderTaskService.doStartSpider(info);
        map.put("taskId", taskId);
        return JSONObject.toJSONString(new Result(true, map));
    }

    @ResponseBody
    @RequestMapping(value = "/info/test", produces = "application/json;charset=utf-8")
    public String testSpiderInfo(SpiderInfo spiderInfo, HttpServletRequest request){
        try{
            if (spiderInfo.getId() == null) {
                spiderInfo.setCreateTime(new Date());
                spiderInfoService.insert(spiderInfo);
            } else {
                spiderInfoService.updateById(spiderInfo);
            }
            List<Webpage> list = spiderTaskService.doTestSpiderInfo(spiderInfo);
            return JSONObject.toJSONString(new Result(true,list));
        }catch (Exception e){
            e.printStackTrace();
        }
        return JSONObject.toJSONString(new Result(false));
    }
}
