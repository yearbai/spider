package com.moye.crawler.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.sys.SysRoleMenu;

import java.util.List;

/**
 * <p>
  * 角色和菜单关联表 Mapper 接口
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysRoleMenuDao extends BaseMapper<SysRoleMenu> {
    /**
     * 根据用户Id获取用户所在角色的权限
     */
    public List<Integer> selectRoleMenuIdsByUserId(Integer uid);

    void insertBatch(List<SysRoleMenu> list);

}