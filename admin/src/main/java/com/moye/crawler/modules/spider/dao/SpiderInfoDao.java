package com.moye.crawler.modules.spider.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.spider.SpiderInfo;
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
public interface SpiderInfoDao extends BaseMapper<SpiderInfo> {

    List<SpiderInfo> list(Page<SpiderInfo> page , @Param("map")Map<String, String> map);
}