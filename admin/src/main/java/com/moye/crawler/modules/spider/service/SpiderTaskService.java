package com.moye.crawler.modules.spider.service;

import com.baomidou.mybatisplus.plugins.Page;

import com.baomidou.mybatisplus.service.IService;
import com.moye.crawler.core.spider.common.Webpage;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.entity.spider.SpiderTask;

import javax.management.JMException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
public interface SpiderTaskService extends IService<SpiderTask> {


    public String doStartSpider(SpiderInfo info);

    SpiderTask getTaskById(String uuid);

    public List<Webpage> doTestSpiderInfo(SpiderInfo info) throws JMException;

    Page<SpiderTask> findSipderTaskList(Page<SpiderTask> page, Map<String, String> paramMap);

    Page<Map<String, Object>> findSpiderDataPage(Page<Webpage> page, Map<String,String> stringMap);
}
