package com.zkz.message.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MsgReceive {
    private Long id;

    private String no;

    private Date createtime;

    private Integer flag;

}