package com.moye.crawler.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.sys.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
  * 菜单表 Mapper 接口
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysMenuDao extends BaseMapper<SysMenu> {
    List<String> selectMenuIdsByuserId(Integer uid);

    List<String> selectResourceByUid(@Param("userid") Integer uid);

    List<SysMenu> selectMenuByRoleId(@Param("roleid") Integer roleid);

}