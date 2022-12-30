package baedalmate.baedalmate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = String.format("%s API", StringUtils.capitalize("Baedalmate"));
        Info info = new Info()
                .version("v1.0.0")
                .title("배달메이트")
                .description("토큰 입력 시 Bearer 꼭 추가!!");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(info);
    }

//    @Data
//    static class Page {
//        @ApiModel(value = "페이지 번호(0..N)")
//        private Integer page;
//
//        @ApiModel(value = "페이지 크기", allowableValues = "range[0, 100]")
//        private Integer size;
//
//        @ApiModel(value = "정렬(사용법: deadlineDate | score | view)")
//        private String sort;
//    }
}