package com.moye.crawler.modules.spider.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.moye.crawler.entity.spider.SpiderInfo;
import com.moye.crawler.modules.spider.dao.SpiderInfoDao;
import com.moye.crawler.modules.spider.service.SpiderInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.apache.http.annotation.ThreadSafe;


import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
@Service
public class SpiderInfoServiceImpl extends ServiceImpl<SpiderInfoDao, SpiderInfo> implements SpiderInfoService {

    @Autowired
    private SpiderInfoDao spiderInfoDao;

    @Override
    public Page<SpiderInfo> findSipderInfoList(Page<SpiderInfo> page, Map<String, String> paramMap) {
        page.setRecords(spiderInfoDao.list(page, paramMap));

        return page;
    }
}
