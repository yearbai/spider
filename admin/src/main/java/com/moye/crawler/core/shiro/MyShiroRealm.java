package com.moye.crawler.core.shiro;

/**
 * Created by moye on 2017/11/17.
 */

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.moye.crawler.entity.sys.SysUser;
import com.moye.crawler.modules.sys.service.SysRoleMenuService;
import com.moye.crawler.modules.sys.service.SysUserRoleService;
import com.moye.crawler.modules.sys.service.SysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Set;

/**
 * Created by yangqj on 2017/4/21.
 */

public class MyShiroRealm extends AuthorizingRealm {


    @Resource
    private SysUserService userService;

    @Autowired
    private SysRoleMenuService roleMenuService;

    @Autowired
    private SysUserRoleService userRoleService;


    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户的输入的账号.
        UsernamePasswordToken user = (UsernamePasswordToken) token;
        SysUser sysUser = null;
        try{
            sysUser = userService.selectOne(new EntityWrapper<SysUser>().eq("account", user.getUsername()));
        }catch (Exception e){
            e.printStackTrace();
        }
        if (sysUser == null) {
            throw new UnknownAccountException();
        }
        if (sysUser.getStatus() == 2) {
            throw new LockedAccountException();
        }
        SecurityUtils.getSubject().getSession().setAttribute("user", sysUser);
        //用户名，数据库中的密码,reaml名称
        //SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user.getUsername(),sysUser.getPassword(),getName());
        //盐值加密
        // 密码加盐处理
//        String source = sysUser.getSalt();
//        ByteSource credentialsSalt = new Md5Hash(source);
        ByteSource byteSource = ByteSource.Util.bytes(user.getUsername());
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(sysUser,sysUser.getPassword(),byteSource,getName());
//        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(sysUser, sysUser.getPassword(), credentialsSalt, getName());
        // 当验证都通过后，把用户信息放在session里
//        Session session = SecurityUtils.getSubject().getSession();
//        session.setAttribute("userSession", sysUser);
//        session.setAttribute("userSessionId", sysUser.getId());
        //这样前端页面可取到数据

        return info;
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();//User{id=1, username='admin', password='3ef7164d1f6167cb9f2658c07d3c2f0a', enable=1}
        Set<String> roles = userRoleService.findRolesByUid(user.getId());
        Set<String> resources = roleMenuService.findMenusByUid(user.getId());
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        System.out.println(resources);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(resources);
        return info;
    }

    public static String getPassword(String password, String salt){
        String hashAlgorithmName = "MD5";
        int hashIterations = 1024;
        ByteSource credentialsSalt = ByteSource.Util.bytes(salt);
        Object obj = new SimpleHash(hashAlgorithmName, password, credentialsSalt, hashIterations);
        return obj.toString();
    }
    /**
     * 密码加密 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        // MD5,"原密码","盐",加密次数
        String hashAlgorithmName = "MD5";
        String credentials = "123456";
        int hashIterations = 1024;
        ByteSource credentialsSalt = ByteSource.Util.bytes("admin");
        Object obj = new SimpleHash(hashAlgorithmName, credentials, credentialsSalt, hashIterations);
        System.out.println(obj);

    }
}