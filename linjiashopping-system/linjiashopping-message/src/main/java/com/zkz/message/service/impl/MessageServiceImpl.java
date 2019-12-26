package com.zkz.message.service.impl;


import com.alibaba.druid.support.json.JSONUtils;
import com.zkz.common.api.CommonResult;
import com.zkz.common.api.config.ProjectConfig;
import com.zkz.common.api.util.*;
import com.zkz.message.entity.CheckEmailCodeProperties;
import com.zkz.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Service
public class MessageServiceImpl implements MessageService {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private CheckEmailCodeProperties  checkEmailCodeProperties;

    @Override
    public CommonResult<String> sendMessage(String to, String ip, int type){
        if (type == 1){
            return sendEmial(to,ip);

        }else {
            return sendMsm(to,ip);
        }

    }
    @Override
    public CommonResult<Map<String,String>> checkEmail(String data, String to){
        if (data.isEmpty()){
            return CommonResult.failed("请输入验证码");
        }

        String key =  ProjectConfig.EMAILCODE + to;
        if (this.jedisUtil.exists(key)) {
            String vcode = this.jedisUtil.get(key);
            if (this.jedisUtil.exists("emailuse:" + vcode)) {
                return CommonResult.validateFailed("链接已经使用过了，请重新获取");
            }

                String s = EncryptionUtil.AESEnc(ProjectConfig.AESKEYACTIVECODE, vcode);
                if ( s.equals(data)) {
                 jedisUtil.setnx("emailuse:" + vcode, vcode);
                return resultToken(to);

                }else {
                  return   CommonResult.validateFailed("验证码错误，请重新获取验证码");
                }

        }


        return CommonResult.validateFailed("验证码过期，请重新获取验证码");


    }
    @Override
    public String  success(String userName) {


        String token = jwtTokenUtil.generateTokenByUserName(userName);
        //将用户令牌+用户名存入rides中。设置3分钟后失效
       jedisUtil.setex(ProjectConfig.TOKENPHONE+userName,180,token);

        jedisUtil.setex(ProjectConfig.TOKENJWT+token,180, userName);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!");
        return token;
    }


    @Override
    public CommonResult<Map<String,String> > checkCode(String data, String to){

        String key = ProjectConfig.SMSCODE+to;
        String vcode1 = jedisUtil.get(key);

            if (jedisUtil.exists(key)){
                String vcode = jedisUtil.get(key);

                    if(vcode.equals(data)) {


                        return resultToken(to);
                    }else {
                        return CommonResult.validateFailed("验证码错误，请重新输入");
                    }

            }
            return CommonResult.validateFailed("验证码过期，请重新获取验证码");

    }
    private CommonResult<String> sendMsm(String to, String ip) {
        int count = 0;
        String c = jedisUtil.get(ProjectConfig.SMSPREDAY +to);
        if (c != null && c.matches("[0-9]{1,2}")) {
            count = Integer.parseInt(c);
            if (count >= 20) {
                return CommonResult.validateFailed("亲，一天只能 发送20条短信息哦");

            }
            if (jedisUtil.exists(ProjectConfig.SMSPRELIMIT + to)) {
                return  CommonResult.validateFailed("一个 手机号一分钟只能发送一次短信");
            }
        }
        int code = CodeUtil.createNum(6);
        int flag =  3;
        String info = "";
        boolean isfalg = false;
        if(jedisUtil.exists(ProjectConfig.SMSCODE+to) ){
            //上次的验证码还没失效
            code=Integer.parseInt(jedisUtil.get(ProjectConfig.SMSCODE+to));

            if (jedisUtil.exists("emailuse:" + code)){
                code = CodeUtil.createNum(6);
                isfalg=true;
            }
        }else {
            code = CodeUtil.createNum(6);
            isfalg=true;
        }
        info="发送短信验证码："+code;
        //验证码 三分钟有效
        /*SmsUtil.mobileQuery(to,code);*/
        ArrayList<String> codes = new ArrayList<>();
        codes.add(code+"");
        TXSmsUtil.mobileQuery(to,codes);
        //1分钟
        jedisUtil.setex(ProjectConfig.SMSPRELIMIT+to,60,"短信发送限制");
        //1天
        jedisUtil.setex(ProjectConfig.SMSPREDAY+to, TimeUtil.getLastSeconds(),(count+1)+"");


        if (isfalg) {
            String s3 = jedisUtil.setex(ProjectConfig.SMSCODE + to, 600, code + "");

        }
        return CommonResult.success("发送成功");
    }

    private CommonResult<String> sendEmial(String to, String ip){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("邮箱验证码");
        int code = CodeUtil.createNum(6);
        String mw = EncryptionUtil.AESEnc(ProjectConfig.AESKEYACTIVECODE, JSONUtils.toJSONString(code));
        String e = EncryptionUtil.AESEnc(ProjectConfig.AESKEYACTIVECODE, JSONUtils.toJSONString( CodeUtil.createNum(6)));

        String url = checkEmailCodeProperties.getUrl() + "?data=" + mw + "&to=" + to + "&code=" + e;
      /*  String url = "?data=" + mw + "&to=" + to + "&code=" + e;*/
        System.out.println(url);


        Context context = new Context();
        context.setVariable("apiUrl" , url);

       /* context.setVariable("apiUrl" , code);*/
        String content = templateEngine.process("email" , context);
       try {
            sendHtmlMail(to, "邮箱验证" , content, ip);

           jedisUtil.setex(ProjectConfig.EMAILCODE + to, 60 * 60 * 60, code + "");
           return CommonResult.success("ok");
         /*   messageLogMappe.insert(log);*/
        }catch ( MessagingException ex){
           return CommonResult.failed("发送失败");


        }
      /*  jedisUtil.setex(ProjectConfig.EMAILCODE + to, 60 * 60 * 60, code + "");
        return CommonResult.success("ok");*/


    }

    private void sendHtmlMail(String to, String subject, String content, String ip) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
        String info = "发送HTML邮件：";

    }

    private CommonResult<Map<String, String>> resultToken(String to){
        String token = jwtTokenUtil.generateTokenByUserName(to);
        //将用户令牌+用户名存入rides中。设置3分钟后失效
        jedisUtil.setex(ProjectConfig.TOKENPHONE+to,180,token);

        jedisUtil.setex(ProjectConfig.TOKENJWT+token,180, to);
        /*  String token = jedisUtil.get(ProjectConfig.TOKENPHONE+to);*/
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Basic"+token);
        return CommonResult.success(tokenMap);
    }

}
