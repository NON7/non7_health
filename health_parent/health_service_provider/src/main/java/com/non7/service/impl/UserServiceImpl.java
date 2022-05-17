package com.non7.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.non7.dao.PermissionDao;
import com.non7.dao.RoleDao;
import com.non7.dao.UserDao;
import com.non7.pojo.Permission;
import com.non7.pojo.Role;
import com.non7.pojo.User;
import com.non7.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 用户服务
 */
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;


//    根据用户名查询数据库获取用户信息和关联的角色信息，同时需要查询角色关联的权限信息
    public User findByUserName(String username) {
        User user = userDao.findByUserName(username);//用户基本信息，不包含用户的角色
        if(user==null){
            return null;
        }
        Integer userId = user.getId();
//        根据用户ID查询对应的角色
        Set<Role> roles = roleDao.findByUserId(userId);
        for (Role role : roles) {
            Integer roleId = role.getId();
//            根据角色ID查询
            Set<Permission> permissions = permissionDao.findByRoleId(roleId);
            role.setPermissions(permissions);//让角色关联权限
        }

        user.setRoles(roles);//让用户关联角色
        return user;
    }
}
