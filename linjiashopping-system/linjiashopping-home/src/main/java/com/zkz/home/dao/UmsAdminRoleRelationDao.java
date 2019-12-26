package com.zkz.home.dao;


import com.zkz.model.UmsAdminRoleRelation;
import com.zkz.model.UmsPermission;
import com.zkz.model.UmsRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台用户与角色管理自定义Dao
 * Created by macro on 2018/10/8.
 */
public interface UmsAdminRoleRelationDao {
    /**
     * 批量插入用户角色关系
     */
    @Transactional
    int insertList(@Param("list") List<UmsAdminRoleRelation> adminRoleRelationList);

    /**
     * 获取用于所有角色
     */
    List<UmsRole> getRoleList(@Param("adminId") Long adminId);

    /**
     * 获取用户所有角色权限
     */
    List<UmsPermission> getRolePermissionList(@Param("adminId") Long adminId);

    /**
     * 获取用户所有权限(包括+-权限)
     */
    List<UmsPermission> getPermissionList(@Param("adminId") Long adminId);
    /**
     * 给用户分配角色
     * */
     int insert(@Param("admin_id") Long adminId, @Param("role_id") Long roleId);



    int deleteAdminRoleByAdminId(@Param("admin_id") Long id);


}
