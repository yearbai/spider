package com.moye.crawler.modules.spider.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.moye.crawler.entity.spider.SpiderInfo;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
public interface SpiderInfoService extends IService<SpiderInfo> {

    Page<SpiderInfo> findSipderInfoList(Page<SpiderInfo> page, Map<String, String> paramMap);
}
