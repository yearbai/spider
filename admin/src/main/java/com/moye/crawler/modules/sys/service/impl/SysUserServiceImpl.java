package com.moye.crawler.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;

import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.SysUserRole;
import com.moye.crawler.modules.sys.dao.SysUserDao;
import com.moye.crawler.modules.sys.dao.SysUserRoleDao;
import com.moye.crawler.modules.sys.service.SysUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserService {
    @Autowired
    private SysUserDao userDao;

    @Autowired
    private SysUserRoleDao userRoleDao;

    @Override
    public Page<SysUser> selectUserPage(Page<SysUser> page, SysUser user) {
//        EntityWrapper<User> wrapper = new EntityWrapper<>();
//        wrapper.orderBy("id", false);
//        page.setRecords(userDao.selectPage(page,wrapper));
        page.setRecords(userDao.selectUserList(page, user));
        return page;
    }

    @Override
    public Page<SysUser> queryPageList(Page<SysUser> page, Map<String, Object> map) {
        page.setRecords(userDao.queryPageList(page, map));
        return page;
    }

    @Transactional
    @Override
    public boolean deleteUserByIdAndRole(String id) {
        // 拼接用户表删除list
        String[] split = id.split(",");
        List<String> userId = new ArrayList<>();
        for (String item:split) {
            userId.add(item);
        }
        // 删除用户表数据
        Integer integer = baseMapper.deleteBatchIds(userId);

        // 删除用户角色关联表数据
        EntityWrapper<SysUserRole> userRoleEntityWrapper = new EntityWrapper<>();
        userRoleEntityWrapper.in("userId", id);
        userRoleDao.delete(userRoleEntityWrapper);

        return true;
    }

    @Override
    public boolean deleteUser(String id) {
        //1、删除用户角色
        EntityWrapper<SysUserRole> wrapper = new EntityWrapper<>();
        wrapper.eq("userid", id);
        userRoleDao.delete(wrapper);
        //2、删除用户
        userDao.delete(new EntityWrapper().eq("id", id));
        return true;
    }

    @Override
    public Page<SysUser> queryRoleUserList(Page<SysUser> page, Map<String, Object> map) {
        page.setRecords(userDao.queryRoleUserList(page,map));
        return page;
    }
}
