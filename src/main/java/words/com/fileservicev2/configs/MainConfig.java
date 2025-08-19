package words.com.fileservicev2.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import words.backend.authmodule.configs.WordSecurityMainConfig;
import words.com.fileservicev2.db.daos.impls.DaoConfig;
import words.com.fileservicev2.domain.services.impls.DomainServiceConfig;
import words.com.fileservicev2.net.clients.impls.NetClientConfig;
import words.com.subscribevalidationmodule.configs.SubscriptionModulMainConfig;

@Configuration
@Import(
        {
                SubscriptionModulMainConfig.class,
                DaoConfig.class,
                NetClientConfig.class,
                DomainServiceConfig.class,
                WordSecurityMainConfig.class
        }
)
public class MainConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .info(new Info().title("File Service V2 API").version("1.0"))
                        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                        .components(new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes("bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")));
        }
}
