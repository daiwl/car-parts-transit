package ru.d4nik.carparts.components.autocompass.infra;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Сервис для публикации автозапчастей на маркетплейсах"));
    }

    @Bean
    public GroupedOpenApi advertisingApi() {
        return GroupedOpenApi.builder()
                .displayName("Женя Киселев")
                .group("kiselev")
                .packagesToScan("ru.d4nik.carparts.components.kiselev")
                .build();
    }

}
