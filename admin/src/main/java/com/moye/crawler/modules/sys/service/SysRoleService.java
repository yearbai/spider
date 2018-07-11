package com.moye.crawler.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.moye.crawler.entity.sys.SysRole;
import com.moye.crawler.entity.sys.SysUserRole;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysRoleService extends IService<SysRole> {

    List<SysRole> selectRoleList(SysRole role);
    /**
     * 删除角色表并且删除角色权限表关联数据
     * @param id
     * @return
     */
    boolean deleteRoleByIdAndPermission(String id);

    /**
     * 分配用户角色
     * @param userRoles
     * @return
     */
    boolean modifyUserRole(List<SysUserRole> userRoles);

    boolean saveOrUpdate(SysRole role);
}
