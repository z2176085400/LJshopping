package com.zkz.message.dao;


import com.zkz.message.entity.MsgReceive;

import java.util.List;

public interface MsgReceiveMapper {

    int insert(MsgReceive record);

    List<MsgReceive> selectAll();
}