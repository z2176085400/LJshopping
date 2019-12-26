package com.zkz.message.controller;

import com.zkz.common.api.CommonResult;
import com.zkz.common.api.config.ProjectConfig;
import com.zkz.common.api.util.JedisUtil;
import com.zkz.common.api.util.JwtTokenUtil;
import com.zkz.message.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@Api(tags = "UmsAdminController", description = "验证码服务")
@RequestMapping("/message")
@CrossOrigin
public class MessageController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private  String tokenHead;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private MessageService messageService;
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @ApiOperation(value = "发送短信或者邮箱验证码")
    @RequestMapping(value = "/sendcodeMessage", method = RequestMethod.POST)
    @ResponseBody
    CommonResult<String> sendMessage(String to, HttpServletRequest request ) {
        try {
            log.info("-----------发送短信或者邮箱验证码接口请求参数-----------："+to);
            int type = 0;
            boolean b = to.matches("^1(3|4|5|6|7|8|9)\\d{9}$");
            if (to != null && to.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
                type = 1;
            } else if (to != null && to.matches("^1(3|4|5|6|7|8|9)\\d{9}$")) {
                type = 2;
            }
            else {
                return CommonResult.validateFailed("格式错误");
            }
            return messageService.sendMessage(to, request.getRemoteAddr(),type);
        }catch (Exception e){
            log.error("发送短信或者邮箱验证码异常");
            return CommonResult.failed();
        }

    }

    @ApiOperation(value = "校验验证码")
    @RequestMapping(value = "/checkcode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String,String> > checkcode(@RequestParam("to") String to, @RequestParam("data") String  data) {
        try {
            log.info("-----------校验验证码接口请求参数-----------：发送至"+to+"验证码是"+data);
            if (to != null && to.matches("^1(3|4|5|6|7|8|9)\\d{9}$")) {
            return messageService.checkCode(data,to);
             }else {

                return CommonResult.validateFailed("格式错误");

            }


        }catch (Exception e){
            log.error("校验验证码接口异常");
            return CommonResult.failed();
        }

    }
    @ApiOperation(value = "邮箱验证码登陆")
    @RequestMapping(value = "/emailLogin", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<String > emailLogin(HttpServletRequest request, @RequestParam("to") String to) {
        try {
            log.info("-----------邮箱验证码登陆接口请求参数-----------："+to);
            String token = jedisUtil.get(ProjectConfig.TOKENPHONE+to);
            if (to != null &&  token != null) {

                String userName =jwtTokenUtil.getUserNameFromToken(token);

                return to.equals(userName)? CommonResult.success(token): CommonResult.failed("邮箱验证登陆失败,请重新获取");
            }else {

                return CommonResult.failed("请到邮箱验证");

            }

        }catch (Exception e){
            log.error("邮箱验证码登陆接口异常");
            return CommonResult.failed();
        }



    }

}
