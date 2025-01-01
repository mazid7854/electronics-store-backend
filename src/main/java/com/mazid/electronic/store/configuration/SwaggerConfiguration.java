package com.mazid.electronic.store.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {
    @Bean

    public OpenAPI myCustomConfig() {
        // Define the server list
        Server localServer = new Server()
                .url("http://localhost:9090")
                .description("Local Server");


        Server productionServer = new Server()
                .url("https://api.example.com")
                .description("Production Server");

        return new OpenAPI().info(
                new io.swagger.v3.oas.models.info.Info()
                        .title("Electronic Store")
                        .version("1.0.0")
                        .description("Backend for Electronic Store by- Mazid")

        )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components( new Components().addSecuritySchemes(
                        "bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ));
    }
}
