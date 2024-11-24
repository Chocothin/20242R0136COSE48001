package com.example.choco_planner.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class SwaggerConfig {

    private final Environment env;

    public SwaggerConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public OpenAPI api() {
        boolean isLocal = isLocalProfile();

        String serverUrl = isLocal
                ? "http://localhost:8000"
                : "https://monthly-madge-choco-planner-59fb550a.koyeb.app";

        return new OpenAPI()
                .info(defaultInfo())
                .addServersItem(new Server().url(serverUrl).description(isLocal ? "Local Server" : "Production Server"));
    }

    private Info defaultInfo() {
        return new Info()
                .title("RIO FREE API")
                .description("RIO FREE API Description");
    }


    private boolean isLocalProfile() {
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("local".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
