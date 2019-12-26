package com.zkz.home.service.impl;

import com.zkz.home.dao.UmsPermissionMapper;
import com.zkz.home.service.UmsPermissionService;
import com.zkz.entity.UmsPermissionNode;
import com.zkz.model.UmsPermission;
import com.zkz.model.UmsPermissionExample;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class UmsPermissionServiceImpl implements UmsPermissionService {
    @Autowired
    private UmsPermissionMapper permissionMapper;

    @Override
    public int create(UmsPermission permission) {
        permission.setStatus(1);
        permission.setCreateTime(new Date());
        permission.setSort(0);
        return permissionMapper.insert(permission);
    }

    @Override
    public int update(Long id, UmsPermission permission) {
        permission.setId(id);
        return permissionMapper.updateByPrimaryKey(permission);
    }

    @Override
    public int delete(List<Long> ids) {
        UmsPermissionExample example = new UmsPermissionExample();
        example.createCriteria().andIdIn(ids);
        return permissionMapper.deleteByExample(example);
    }

    @Override
    public List<UmsPermissionNode> treeList() {
        return null;
    }

    @Override
    public List<UmsPermission> list() {
        return null;
    }
}
