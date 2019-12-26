package com.zkz.message.controller;

import com.zkz.common.api.CommonResult;
import com.zkz.message.service.MessageService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class CheckCodeController {
    @Autowired
    private MessageService messageService;
    private static final Logger log = LoggerFactory.getLogger(CheckCodeController.class);
    @ApiOperation(value = "校验验证码")
    @RequestMapping(value = "/message/checkEmail", method = RequestMethod.GET)


    public String checkcode(@RequestParam("to") String to, @RequestParam("data") String  data) {
        try {
            int type = 0;
            if (to != null && to.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
                CommonResult<Map<String, String>> mapCommonResult = messageService.checkEmail(data, to);
                if (mapCommonResult.getCode()==200){
                    return "pass.html";
                }
            }
        }catch (Exception e){
            log.error("校验验证码异常");
        }


        return "emailError.html";
    }
}
