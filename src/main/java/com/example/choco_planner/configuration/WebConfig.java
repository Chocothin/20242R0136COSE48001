package com.example.choco_planner.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    private final AuthPrincipalArgumentResolver authPrincipalArgumentResolver;

//    public WebConfig(AuthPrincipalArgumentResolver authPrincipalArgumentResolver) {
//        this.authPrincipalArgumentResolver = authPrincipalArgumentResolver;
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedOrigins(
                        "http://localhost:8000", // 로컬 Swagger UI
                        "https://monthly-madge-choco-planner-59fb550a.koyeb.app" // 배포된 Swagger UI
                )
                .allowCredentials(true);
    }

//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(authPrincipalArgumentResolver);
//    }
}
