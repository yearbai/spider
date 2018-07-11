package com.moye.crawler.modules.sys.dao;

import com.baomidou.mybatisplus.plugins.Page;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 用户角色关联表 Mapper 接口
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysUserRoleDao extends BaseMapper<SysUserRole> {

    void batchSave(List<SysUserRole> list);

    List<SysUser> findUnSelectedUserList(Page<SysUser> page, @Param("map")Map<String, String> user);

    List<SysUser> findSelectedUserList(Page<SysUser> page,@Param("map")Map<String, String>  user);
}