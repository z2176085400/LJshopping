package com.zkz.home.dao;

import com.zkz.model.UmsAdmin;
import com.zkz.model.UmsAdminExample;

import java.util.List;

public interface UserMapper {
    List<UmsAdmin> selectByExample(UmsAdminExample example);

}
