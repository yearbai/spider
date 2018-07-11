package com.moye.crawler.modules.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.moye.crawler.common.utils.TreeNode;
import com.moye.crawler.common.utils.TreeUtil;
import com.moye.crawler.entity.sys.SysMenu;
import com.moye.crawler.entity.sys.SysRoleMenu;
import com.moye.crawler.entity.sys.vo.TreeMenu;
import com.moye.crawler.entity.sys.vo.TreeMenuAllowAccess;
import com.moye.crawler.entity.sys.vo.ZNodes;
import com.moye.crawler.modules.sys.dao.SysMenuDao;
import com.moye.crawler.modules.sys.dao.SysRoleMenuDao;
import com.moye.crawler.modules.sys.service.SysMenuService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.moye.crawler.modules.sys.service.SysRoleMenuService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenu> implements SysMenuService {
    /**
     * 菜单服务
     */
    @Autowired
    private SysMenuDao sysMenuDao;
    /**
     * 角色菜单关系服务
     */
    @Autowired private SysRoleMenuDao sysRoleMenuDao;


    @Autowired
    private SysRoleMenuService roleMenuService;

    /**
     * 获取指定用户拥有的菜单
     */
    @Cacheable(value = "permissionCache", key = "#uid")
    @Override
    public List<String> selectMenuIdsByuserId(Integer uid) {
        return sysMenuDao.selectMenuIdsByuserId(uid);
    }
    /**
     * 获取指定用户的菜单
     * @param  menuIds 当前用户所在角色拥有的权限ID集合
     * @param  pid 菜单父ID
     */
    @Override
    public List<TreeMenu> selectTreeMenuByMenuIdsAndPid(List<Integer> menuIds, Integer pid) {
        EntityWrapper<SysMenu> ew = new EntityWrapper<SysMenu>();
        //ew.orderBy("sort", true);
        ew.eq("pid", pid);
        ew.in("id", menuIds.size() > 0 ? menuIds : Lists.newArrayList(RandomStringUtils.randomNumeric(30)));
        List<SysMenu> sysMenus = this.selectList(ew);
        List<TreeMenu> treeMenus = new ArrayList<TreeMenu>();
        for(SysMenu sysMenu : sysMenus){
            TreeMenu treeMenu = new TreeMenu();
            treeMenu.setSysMenu(sysMenu);
            if(sysMenu.getLevels() < 2){
                treeMenu.setChildren(selectTreeMenuByMenuIdsAndPid(menuIds,sysMenu.getId()));
            }
            treeMenus.add(treeMenu);
        }
        return treeMenus;
    }
    /**
     * 获取当前用户的菜单
     */
//    @Cacheable(value = "menuCache", key = "#uid")
    @Override
    public List<TreeMenu> selectTreeMenuByUserId(String uid) {

        /**
         * 当前用户二级菜单权限
         */
        List<Integer> menuIds = sysRoleMenuDao.selectRoleMenuIdsByUserId(NumberUtils.toInt(uid));
        return selectTreeMenuByMenuIdsAndPid(menuIds, 0);
    }
    /**
     * 获取指定用户拥有权限
     * @param  menuIds 该角色拥有的权限ID集合
     * @param  pid 菜单父ID
     */
    @Override
    public List<TreeMenuAllowAccess> selectTreeMenuAllowAccessByMenuIdsAndPid(List<Integer> menuIds, Integer pid) {
        // TODO Auto-generated method stub
        EntityWrapper<SysMenu> ew = new EntityWrapper<SysMenu>();
        ew.orderBy("sort", true);
        ew.eq("pid", pid);
        List<SysMenu> sysMenus = this.selectList(ew);

        List<TreeMenuAllowAccess> treeMenuAllowAccesss = new ArrayList<TreeMenuAllowAccess>();
        for(SysMenu sysMenu : sysMenus){
            TreeMenuAllowAccess treeMenuAllowAccess = new TreeMenuAllowAccess();
            treeMenuAllowAccess.setSysMenu(sysMenu);
            /**
             * 是否有权限
             */
            if(menuIds.contains(sysMenu.getId())){
                treeMenuAllowAccess.setAllowAccess(true);
            }
            /**
             * 子节点
             */
            if(sysMenu.getLevels() < 3){
                treeMenuAllowAccess.setChildren(selectTreeMenuAllowAccessByMenuIdsAndPid(menuIds,sysMenu.getId()));
            }
            treeMenuAllowAccesss.add(treeMenuAllowAccess);
        }
        return treeMenuAllowAccesss;
    }

    @Override
    public List<ZNodes> findPermissionZTreeNodes(Integer roleId) {
        // 查询所有权限列表
        EntityWrapper<SysMenu> permissionEntityWrapper = new EntityWrapper<>();
        //permissionEntityWrapper.orderBy("sort", true);
        List<SysMenu> permissions = baseMapper.selectList(permissionEntityWrapper);

        // 查询角色拥有那些权限
        EntityWrapper<SysRoleMenu> rolePermissionEntityWrapper = new EntityWrapper<>();
        rolePermissionEntityWrapper.eq("roleId", roleId);
        List<SysRoleMenu> rolePermissions = sysRoleMenuDao.selectList(rolePermissionEntityWrapper);

        List<ZNodes> zNodes = new ArrayList<>();
        permissions.forEach(p -> {
            ZNodes node = new ZNodes();
            BeanUtils.copyProperties(p, node);
            node.setName(p.getName());
            // 设置展开节点
            if (p.getLevels() == 1 || p.getLevels() == 2) {
                node.setOpen(true);
            }
            // 设置角色拥有的权限选中
            rolePermissions.forEach(rp -> {
                if (p.getId() == rp.getId()) {
                    node.setChecked(true);
                }
            });
            zNodes.add(node);
        });
        return zNodes;
    }

    @Transactional
    @Override
    public boolean modifyRolePermission(List<SysRoleMenu> rolePermissions) {
        // 删除原表角色权限
        EntityWrapper<SysRoleMenu> rolePermissionEntityWrapper = new EntityWrapper<>();
        rolePermissionEntityWrapper.eq("roleId", rolePermissions.get(0).getRoleid());
        sysRoleMenuDao.delete(rolePermissionEntityWrapper);

        // 新增角色权限 并返回处理结果
        return roleMenuService.insertBatch(rolePermissions);
    }

    @Transactional
    @Override
    public boolean deletePermissionRole(String id) {
        // 删除关联表数据
        EntityWrapper<SysRoleMenu> rolePermissionEntityWrapper = new EntityWrapper<>();
        rolePermissionEntityWrapper.eq("pid", id);
        sysRoleMenuDao.delete(rolePermissionEntityWrapper);

        // 删除权限表数据
        Integer result = baseMapper.deleteById(Long.valueOf(id));

        return result >= 1 ? true : false;
    }

    @Override
    public List<TreeNode> getTreeData() {
        // 获取数据
        List<SysMenu> funcs = sysMenuDao.selectList(new EntityWrapper<SysMenu>().eq( "status",1 ).orderBy("id",true));
        List<TreeNode> nodeList = new LinkedList<TreeNode>();
        for (SysMenu func : funcs) {
            System.out.println(func.getName() + "   " + func.getPid());
            if(func.getPid() == null){
                continue;
            }
            TreeNode node = new TreeNode();
            node.setText(func.getName());
            node.setId(func.getId() + "");
            node.setParentId(func.getPid() + "");
            node.setLevelCode(func.getLevels()+"");
            node.setIcon(func.getIcon());
            nodeList.add(node);
        }
        // 构造树形结构
        return TreeUtil.getTreeNode(nodeList, "0");
    }

    @Override
    public void saveOrUpdate(SysMenu menu) {
        if(menu.getId() == null){//add
            menu.setPid(menu.getPid() == null ? 0 : menu.getPid());
            sysMenuDao.insert(menu);
        }else{//update
            sysMenuDao.updateById(menu);
        }
    }

    @Override
    public boolean deleteMenu(Integer menuId) {
        EntityWrapper<SysRoleMenu> wrapper = new EntityWrapper<>();
        wrapper.eq("menuid", menuId);
        List<SysRoleMenu> roleMenuList = sysRoleMenuDao.selectList(wrapper);
        if(roleMenuList.size() > 0 ){
            return false;
        }else{
            EntityWrapper<SysMenu> menuEntityWrapper = new EntityWrapper<>();
            menuEntityWrapper.eq("id", menuId);
            sysMenuDao.delete(menuEntityWrapper);
        }
        return true;
    }

    @Override
    public List<TreeNode> getTreeDataByRoleId(String roleId) {
        List<SysMenu> funcs = sysMenuDao.selectMenuByRoleId(NumberUtils.toInt(roleId));
        Map<String, TreeNode> nodelist = new LinkedHashMap<String, TreeNode>();
        for (SysMenu func : funcs) {
            TreeNode node = new TreeNode();
            node.setText(func.getName());
            node.setId(func.getId() + "");
            node.setParentId(func.getPid() + "");
            node.setLevelCode(func.getLevels() + "");
            node.setIcon(func.getIcon());
            nodelist.put(node.getId(), node);
        }
        // 构造树形结构
        return TreeUtil.getNodeList(nodelist);
    }

}
