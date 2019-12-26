package com.zkz.message.dao;


import com.zkz.message.entity.Message;

import java.util.List;

public interface MessageMapper {

    int insert(Message record);

    List<Message> selectAll();
}