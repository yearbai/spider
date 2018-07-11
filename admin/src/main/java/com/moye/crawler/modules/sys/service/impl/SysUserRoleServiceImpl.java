package com.moye.crawler.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;

import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.entity.sys.SysUserRole;
import com.moye.crawler.modules.sys.dao.SysUserRoleDao;
import com.moye.crawler.modules.sys.service.SysUserRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用户角色关联表 服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRole> implements SysUserRoleService {

    @Autowired
    private SysUserRoleDao userRoleDao;

    @Override
    public Set<String> findRolesByUid(Integer userId) {
        List<SysUserRole> list = this.selectList(new EntityWrapper<SysUserRole>().eq("userId", userId));
        Set<String> set = new HashSet<String>();
        for (SysUserRole ur : list) {
            set.add(ur.getRoleid().toString());
        }
        return  set;
    }

    @Override
    public void batchSave(List<SysUserRole> list) {
        System.out.println(
                list.size()
        );
        userRoleDao.batchSave(list);
    }

    @Override
    public void deleteByIds(String ids, String roleId) {
        for(String id : ids.split(",")){
            EntityWrapper<SysUserRole> wrapper = new EntityWrapper();
            System.out.println(id + "    " + roleId);
            wrapper.eq("userid", id).eq("roleid", roleId);
            userRoleDao.delete(wrapper);
        }
    }

    @Override
    public Page<SysUser> findUnSelectedUserList(Page<SysUser> page, Map<String,String> map) {
        page.setRecords(userRoleDao.findUnSelectedUserList(page, map));
        return page;
    }

    @Override
    public Page<SysUser> findSelectedUserList(Page<SysUser> page, Map<String,String> map) {
        page.setRecords(userRoleDao.findSelectedUserList(page, map));
        return page;
    }
}
