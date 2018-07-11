package com.moye.crawler.modules.sys.service;

import com.baomidou.mybatisplus.plugins.Page;

import com.baomidou.mybatisplus.service.IService;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.SysUserRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户角色关联表 服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    Set<String> findRolesByUid(Integer userId);

    void batchSave(List<SysUserRole> list);

    void deleteByIds(String ids, String roleId);

    Page<SysUser> findUnSelectedUserList(Page<SysUser> page, Map<String, String> map);

    Page<SysUser> findSelectedUserList(Page<SysUser> page, Map<String, String> map);
}
