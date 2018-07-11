package com.moye.crawler.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.sys.SysRole;

import java.util.List;

/**
 * <p>
  * 角色表 Mapper 接口
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysRoleDao extends BaseMapper<SysRole> {

    List<SysRole> selectRoleList(SysRole role);
}