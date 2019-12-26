package com.zkz.home.dto;

import com.zkz.model.UmsPermission;
import lombok.Data;

import java.util.List;

@Data
public class Menu  {
   private UmsPermission umsPermission;
   private List<UmsPermission> permissionList;

}
