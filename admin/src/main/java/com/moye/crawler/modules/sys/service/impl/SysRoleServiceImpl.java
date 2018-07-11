package com.moye.crawler.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;

import com.moye.crawler.entity.sys.SysRole;
import com.moye.crawler.entity.sys.SysRoleMenu;
import com.moye.crawler.entity.sys.SysUserRole;
import com.moye.crawler.modules.sys.dao.SysRoleDao;
import com.moye.crawler.modules.sys.dao.SysRoleMenuDao;
import com.moye.crawler.modules.sys.dao.SysUserRoleDao;
import com.moye.crawler.modules.sys.service.SysRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.moye.crawler.modules.sys.service.SysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRole> implements SysRoleService {
    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;
    @Autowired
    private SysUserRoleDao userRoleDao;
    @Autowired
    private SysUserRoleService userRoleService;

    @Autowired
    private SysRoleDao roleDao;

    @Override
    public List<SysRole> selectRoleList(SysRole role) {

        return null;
    }

    @Transactional
    @Override
    public boolean deleteRoleByIdAndPermission(String id) {
        // 拼接角色表删除list
        String[] split = id.split(",");
        List<String> userId = new ArrayList<>();
        for (String item : split) {
            userId.add(item);
        }
        // 删除角色表
        Integer integer = baseMapper.deleteBatchIds(userId);

        // 删除角权限关联表数据
        EntityWrapper<SysRoleMenu> permissionEntityWrapper = new EntityWrapper<>();
        permissionEntityWrapper.in("roleId", id);
        sysRoleMenuDao.delete(permissionEntityWrapper);

        return true;
    }

    @Transactional
    @Override
    public boolean modifyUserRole(List<SysUserRole> userRoles) {
        // 删除用户角色原表数据
        EntityWrapper<SysUserRole> userRoleEntityWrapper = new EntityWrapper<>();
        userRoleEntityWrapper.eq("userId", userRoles.get(0).getUserid());
        userRoleDao.delete(userRoleEntityWrapper);

        // 新增用户角色数据 并 返回
        return userRoleService.insertBatch(userRoles);
    }

    @Override
    public boolean saveOrUpdate(SysRole role) {
        try {
            if (role.getId() == null) {//save
                roleDao.insert(role);
            } else {//update
                roleDao.updateById(role);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
