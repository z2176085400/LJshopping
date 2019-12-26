package com.zkz.message.service;

import com.zkz.common.api.CommonResult;

import java.util.Map;

public interface MessageService {
    CommonResult<String> sendMessage(String to, String ip, int type);

    CommonResult<Map<String,String>> checkEmail(String data, String to);

    CommonResult<Map<String,String>> checkCode(String data, String to);

    String  success(String userName);


}
