package com.moye.crawler.modules.sys.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.moye.crawler.entity.sys.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 管理员表 Mapper 接口
 * </p>
 *
 * @author theodo
 * @since 2018-04-04
 */
public interface SysUserDao extends BaseMapper<SysUser> {
    /**
     * 分页
     */
    List<SysUser> selectUserList(Pagination page, SysUser user);

    List<SysUser> queryPageList(Page<SysUser> page, Map<String, Object> map);

    List<SysUser> queryRoleUserList(Page<SysUser> page,@Param("map") Map<String, Object> map);

}