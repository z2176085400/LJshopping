package com.zkz.home.service.impl;


import com.zkz.home.dao.RoleMapper;
import com.zkz.home.dao.UmsAdminRoleRelationDao;
import com.zkz.home.dao.UmsRolePermissionRelationDao;
import com.zkz.home.dao.UmsRolePermissionRelationMapper;
import com.zkz.home.service.UmsRoleService;
import com.zkz.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UmsRoleServiceImpl implements UmsRoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UmsRolePermissionRelationMapper rolePermissionRelationMapper;
    @Autowired
    private UmsRolePermissionRelationDao rolePermissionRelationDao;
    @Autowired
    private UmsAdminRoleRelationDao umsAdminRoleRelationDao;

    @Override
    public int create(UmsRole role) {
        role.setCreateTime(new Date());
        role.setStatus(1);
        role.setAdminCount(0);
        role.setSort(0);
        return roleMapper.insert(role);

    }

    @Override
    public int update(Long id, UmsRole role) {
        role.setId(id);
        return roleMapper.updateByPrimaryKey(role);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsRoleExample example = new UmsRoleExample();
        example.createCriteria().andIdIn(ids);
        return roleMapper.deleteByExample(example);
    }

    @Override
    public List<UmsPermission> getPermissionList(Long roleId) {
        return rolePermissionRelationDao.getPermissionList(roleId);
    }

    @Override
    public int updatePermission(Long roleId, List<Long> permissionIds) {
        UmsRolePermissionRelationExample example=new UmsRolePermissionRelationExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        rolePermissionRelationMapper.deleteByExample(example);
        List<UmsRolePermissionRelation> relationList = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            UmsRolePermissionRelation relation = new UmsRolePermissionRelation();
            relation.setRoleId(roleId);
            relation.setPermissionId(permissionId);
            relationList.add(relation);
        }
        return rolePermissionRelationDao.insertList(relationList);
    }

    @Override
    public List<UmsRole> list() {
        return roleMapper.selectByExample(new UmsRoleExample());
    }

    @Override
    public int adminAddRole(Long adminId, Long[] rid) {

        List<UmsAdminRoleRelation> umsAdminRoleRelations = new ArrayList<>();
        for (int i = 0;i<rid.length; i++){

            UmsAdminRoleRelation umsAdminRoleRelation = new UmsAdminRoleRelation();
            umsAdminRoleRelation.setAdminId(adminId);
            umsAdminRoleRelation.setRoleId(rid[i]);
            umsAdminRoleRelations.add(umsAdminRoleRelation);

        }
       return umsAdminRoleRelationDao.insertList(umsAdminRoleRelations);
    }



}
