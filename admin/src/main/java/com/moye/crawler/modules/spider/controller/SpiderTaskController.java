package com.moye.crawler.modules.spider.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.base.Preconditions;
import com.moye.crawler.common.constant.CodeConstant;
import com.moye.crawler.common.utils.PageInfo;
import com.moye.crawler.common.utils.QueryCondition;
import com.moye.crawler.common.utils.es.ElasticsearchUtils;
import com.moye.crawler.entity.spider.SpiderTask;
import com.moye.crawler.modules.spider.service.SpiderInfoService;
import com.moye.crawler.modules.spider.service.SpiderTaskService;
import com.moye.crawler.core.spider.common.Webpage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
public class SpiderTaskController {

    @Autowired
    private SpiderTaskService taskService;

    @Autowired
    private SpiderInfoService spiderInfoService;


    /**
     * 任务列表
     */
    @RequestMapping(value = "/task/list")
    public String spiderInfo(String id, HttpServletRequest request){
        request.setAttribute("selectId", id == null ? 0 : id);
        return "/base/spider/spider_task_list";
    }

    @ResponseBody
    @RequestMapping(value = "/task/loadData", produces = "application/json;charset=utf-8")
    public String loadData(String reqObj){
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<SpiderTask> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        Map<String, String> paramMap = queryCondition.getConditionMap();
        Page<SpiderTask> userPage = taskService.findSipderTaskList(page, paramMap);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value = "/data/list")
    public String spiderDataPage(String id, HttpServletRequest request){
        request.setAttribute("selectId", id == null ? 0 : id);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", id);
        SpiderTask task = taskService.selectOne(wrapper);
        request.setAttribute("name", task == null ? "" : task.getName());
        return "/base/spider/spider_data_list";
    }


    @ResponseBody
    @RequestMapping(value = "/data/loadData", produces = "application/json;charset=utf-8")
    public String getSpiderDataList(String reqObj){
        System.out.println("init user loadData");
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        PageInfo pageInfo = new PageInfo();
        if (queryCondition.getPageInfo() == null) {
            pageInfo.setPageSize(10);
        } else {
            pageInfo = queryCondition.getPageInfo();
        }
        Page<Webpage> page = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        Map<String, String> paramMap = queryCondition.getConditionMap();
        Page<Map<String,Object>> userPage = taskService.findSpiderDataPage(page, paramMap);
        System.out.println(userPage.getCurrent() + "   " + userPage.getPages() + "---" + userPage.getSize());
        Map<String, Object> map = new HashMap<>();
        map.put("pageData", userPage);
        return JSONObject.toJSONString(map);
    }


    /**
     * 根据网页id展示网页
     *
     * @param id 网页id
     * @return 展示页
     */
    @RequestMapping(value = "/data/view", method = {RequestMethod.GET})
    public String showWebpageById(String id, HttpServletRequest request) {
        //文章内容
        Preconditions.checkNotNull(StringUtils.isBlank(id),"请选择文章");
        Map<String, Object> map = ElasticsearchUtils.searchDataById( CodeConstant.INDEX_NAME, CodeConstant.TYPE_NAME, id, null);
        request.setAttribute("webpage", map);
        //相关资讯
        List<Webpage> relatedWebpageList = ElasticsearchUtils.moreLikeThis(id,5,1);
        request.setAttribute("relatedWebpageList", relatedWebpageList);
        return "/base/spider/spider_data_view";
    }

}
