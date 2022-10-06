package baedalmate.baedalmate.config;

import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableWebMvc
public class SwaggerConfig implements WebMvcConfigurer {

    TypeResolver typeResolver = new TypeResolver();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) { //spring-security와 연결할 때 이 부분을 작성하지 않으면 404에러가 뜬다.
        registry.addResourceHandler("swagger-ui/")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket api() { //swagger를 연결하기 위한 Bean 작성
        return new Docket(DocumentationType.OAS_30)
                .alternateTypeRules(
                        AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(Page.class))
                )
                .alternateTypeRules(
                        AlternateTypeRules.newRule(typeResolver.resolve(PrincipalDetails.class), typeResolver.resolve(UserToken.class))
                )
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() { //선택
        return new ApiInfoBuilder()
                .title("Baedalmate Release") //자신에게 맞는 타이틀을 작성해준다.
                .description("backend api document") //알맞는 description을 작성해준다.
                .version("0.1") //알맞는 버전을 작성해준다.
                .build();
    }

    @Data
    static class UserToken {
        @ApiModelProperty(value = "QueryString 아니고 헤더임!!! 유의!!! Authorization: Bearer {token}")
        private String token;
    }

    @Data
    static class Page {
        @ApiModelProperty(value = "페이지 번호(0..N)")
        private Integer page;

        @ApiModelProperty(value = "페이지 크기", allowableValues="range[0, 100]")
        private Integer size;

        @ApiModelProperty(value = "정렬(사용법: deadlineDate | score | view)")
        private String sort;
    }
}