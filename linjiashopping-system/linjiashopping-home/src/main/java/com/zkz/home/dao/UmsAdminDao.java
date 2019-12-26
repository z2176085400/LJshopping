package com.zkz.home.dao;

import com.zkz.home.dto.UserDto;

import java.util.List;

public interface UmsAdminDao {
    List<UserDto> getAllAdmin();
    UserDto getAdminById(Integer id);
}
