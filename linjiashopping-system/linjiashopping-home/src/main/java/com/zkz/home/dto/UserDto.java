package com.zkz.home.dto;

import com.zkz.model.UmsAdmin;
import com.zkz.model.UmsRole;
import lombok.Data;

import java.util.List;

@Data
public class UserDto extends UmsAdmin {
    List<UmsRole> nameList;
}
