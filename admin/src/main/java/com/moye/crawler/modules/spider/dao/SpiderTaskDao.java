package com.moye.crawler.modules.spider.dao;

import com.baomidou.mybatisplus.plugins.Page;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.spider.SpiderTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author theodo
 * @since 2018-04-19
 */
public interface SpiderTaskDao extends BaseMapper<SpiderTask> {
    List<SpiderTask> list(Page<SpiderTask> page , @Param("map")Map<String, String> map);
}