package com.example.choco_planner.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*") // 모든 HTTP 메서드 허용
                .allowedOrigins("*") // 모든 Origin 허용
                .allowCredentials(false); // 자격 증명(Credentials) 사용하지 않음
    }

}
