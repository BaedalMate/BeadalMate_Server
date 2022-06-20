package baedalmate.baedalmate.config;

import baedalmate.baedalmate.oauth.filter.OAuth2AccessTokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // IoC 빈(bean)을 등록
@EnableWebSecurity // 필터 체인 관리 시작 어노테이션
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 특정 주소 접근시 권한 및 인증을 위한 어노테이션 활성화
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final OAuth2AccessTokenAuthenticationFilter oAuth2AccessTokenAuthenticationFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.cors();
//        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
//        http.formLogin().disable();

//        http.authorizeRequests()
//                .anyRequest().permitAll();

        http.authorizeRequests()
                .antMatchers("/login/oauth2/*").permitAll() //로그인 화면 접근 가능
                .antMatchers("/").permitAll(); //메인 화면 접근 가능
//        http.oauth2Login()
//                .authorizationEndpoint()
//                .baseUri("/login")
//                .and()
//                .redirectionEndpoint()
//                .baseUri("/login/oauth2/code/*");
//                .and()
//                .userInfoEndpoint()
//                .userService(principalOauth2UserService);
//                .and()
//                .successHandler(oAuth2AuthenticationSuccessHandler)
//                .failureHandler(oAuth2AuthenticationFailureHandler);
//        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(oAuth2AccessTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
