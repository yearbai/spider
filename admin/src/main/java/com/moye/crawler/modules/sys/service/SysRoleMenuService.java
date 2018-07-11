package com.moye.crawler.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.moye.crawler.common.utils.Result;
import com.moye.crawler.entity.sys.SysRoleMenu;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色和菜单关联表 服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    Set<String> findMenusByUid(Integer userId);

    Result saveBatchRoleMenu(String roleId, List<SysRoleMenu> roleMenuList);
}
