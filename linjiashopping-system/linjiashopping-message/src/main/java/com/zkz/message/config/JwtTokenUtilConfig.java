package com.zkz.message.config;

import com.zkz.common.api.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenUtilConfig {
    @Bean
    public JwtTokenUtil creatJU(){
        return new JwtTokenUtil();
    }
}
