package com.moye.crawler.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;

import com.moye.crawler.common.utils.TreeNode;
import com.moye.crawler.entity.sys.SysMenu;
import com.moye.crawler.entity.sys.SysRoleMenu;
import com.moye.crawler.entity.sys.vo.TreeMenu;
import com.moye.crawler.entity.sys.vo.TreeMenuAllowAccess;
import com.moye.crawler.entity.sys.vo.ZNodes;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysMenuService extends IService<SysMenu> {
    /**
     * 获取指定用户拥有的菜单
     */
    List<String> selectMenuIdsByuserId(Integer uid);
    /**
     * 获取指定用户的菜单
     * @param  menuIds 当前用户所在角色拥有的权限ID集合
     * @param  pid 菜单父ID
     */
    List<TreeMenu> selectTreeMenuByMenuIdsAndPid(List<Integer> menuIds, Integer pid);
    /**
     * 获取当前用户的菜单
     */
    List<TreeMenu> selectTreeMenuByUserId(String uid);
    /**
     * 获取指定用户拥有权限
     * @param  menuIds 该角色拥有的权限ID集合
     * @param  pid 菜单父ID
     */
    List<TreeMenuAllowAccess> selectTreeMenuAllowAccessByMenuIdsAndPid(List<Integer> menuIds, Integer pid);



    /**
     * 查询所有权限树形展示 并且选中角色拥有的树节点
     * @param roleId 角色ID
     * @return
     */
    List<ZNodes> findPermissionZTreeNodes(Integer roleId);

    /**
     * 修改角色权限
     * @param rolePermissions
     * @return
     */
    boolean modifyRolePermission(List<SysRoleMenu> rolePermissions);

    /**
     * 删除权限 和 权限角色表数据
     * @param id
     * @return
     */
    boolean deletePermissionRole(String id);


    List<TreeNode> getTreeData();

    void saveOrUpdate(SysMenu menu);

    boolean deleteMenu(Integer menuId);

    List<TreeNode> getTreeDataByRoleId(String roleId);
}
