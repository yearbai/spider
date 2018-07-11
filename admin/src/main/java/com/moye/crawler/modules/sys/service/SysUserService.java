package com.moye.crawler.modules.sys.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.moye.crawler.entity.sys.SysUser;

import java.util.Map;

/**
 * <p>
 * 管理员表 服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysUserService extends IService<SysUser> {

    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user);

    Page<SysUser> queryPageList(Page<SysUser> pageUtil, Map<String, Object> map);

    /**
     * 删除用户表并且删除用户角色关联表数据
     * @param id
     * @return
     */
    public boolean deleteUserByIdAndRole(String id);

    boolean deleteUser(String id);


    Page<SysUser> queryRoleUserList(Page<SysUser> page, Map<String, Object> map);


}
