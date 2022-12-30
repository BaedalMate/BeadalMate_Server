//package baedalmate.baedalmate.config;
//
//import baedalmate.baedalmate.security.user.PrincipalDetails;
//import com.fasterxml.classmate.TypeResolver;
//import lombok.Data;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebMvc
//public class SwaggerConfig implements WebMvcConfigurer {
//
//    TypeResolver typeResolver = new TypeResolver();
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) { //spring-security와 연결할 때 이 부분을 작성하지 않으면 404에러가 뜬다.
//        registry.addResourceHandler("swagger-ui/")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Arrays.asList(new SecurityReference("Authorization", authorizationScopes));
//    }
//
//    @Bean
//    public Docket api() { //swagger를 연결하기 위한 Bean 작성
//        return new Docket(DocumentationType.OAS_30)
//                .alternateTypeRules(
//                        AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(Page.class))
//                )
//                .alternateTypeRules(
//                        AlternateTypeRules.newRule(typeResolver.resolve(PrincipalDetails.class), typeResolver.resolve(UserToken.class))
//                )
//                .securityContexts(Arrays.asList(securityContext()))
//                .securitySchemes(Arrays.asList(apiKey()))
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build();
//    }
//
//    private ApiInfo apiInfo() { //선택
//        return new ApiInfoBuilder()
//                .title("Baedalmate Release") //자신에게 맞는 타이틀을 작성해준다.
//                .description("토큰 입력 시 Bearer 꼭 추가!!") //알맞는 description을 작성해준다.
//                .version("0.1") //알맞는 버전을 작성해준다.
//                .build();
//    }
//
//    private ApiKey apiKey() {
//        return new ApiKey("Authorization", "Authorization", "header");
//    }
//
//    @Data
//    static class UserToken {
//    }
//
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
//}