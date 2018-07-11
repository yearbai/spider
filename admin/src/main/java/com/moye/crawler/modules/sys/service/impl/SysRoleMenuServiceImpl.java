package com.moye.crawler.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.entity.sys.SysRoleMenu;
import com.moye.crawler.modules.sys.dao.SysMenuDao;
import com.moye.crawler.modules.sys.dao.SysRoleMenuDao;
import com.moye.crawler.modules.sys.service.SysRoleMenuService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色和菜单关联表 服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenu> implements SysRoleMenuService {
    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private SysRoleMenuDao roleMenuDao;

    @Override
    public Set<String> findMenusByUid(Integer userId) {
        List<String> list = sysMenuDao.selectResourceByUid(userId);
        return new HashSet<>(list);
    }

    @Override
    public Result saveBatchRoleMenu(String roleId, List<SysRoleMenu> roleMenuList) {
        try{
            //1、删除原有角色菜单
            EntityWrapper<SysRoleMenu> wrapper = new EntityWrapper<>();
            wrapper.eq("roleid",roleId);
            roleMenuDao.delete(wrapper);
            if(roleMenuList.size() > 0){
                //2、批量保存新角色菜单
                roleMenuDao.insertBatch(roleMenuList);
            }

        }catch (Exception e){
            e.printStackTrace();
            return new Result(false);
        }
        return new Result(true);
    }
}
