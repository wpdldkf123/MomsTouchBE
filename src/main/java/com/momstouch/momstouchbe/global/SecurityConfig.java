package com.momstouch.momstouchbe.global;

import com.momstouch.momstouchbe.domain.member.Service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean // 인증 실패 처리 관련 객체
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean // 비밀번호 암호화 객체
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 현장예약 api는 인증과 동시에 예약이라 ignore해야 함.
     */
    @Bean
    public WebSecurityCustomizer configure() {
        return (web -> web.ignoring().mvcMatchers("/h2-console/**",
                "/api/shop/*/menus",
                "/api/order/**"));
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지 설정하는 것,
        config.addAllowedOriginPattern("*"); // e.g. http://domain1.com // *이면 모든 ip에 응답을 허용하겠다.
        config.addAllowedHeader("*"); //모든 header에 응답을 허용하겠다.
        config.addAllowedMethod("*"); //모든 post, get, put, delete, patch 요청을 허용하겠다.
        config.addExposedHeader("Authorization");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
             http
                .addFilter(corsFilter())//@CrossOrigin(인증x), 시큐리티 필터에 등록 인증(o)
                .csrf().ignoringAntMatchers("/h2-console/**").disable()
                .headers().frameOptions().disable().and()
                .httpBasic().disable()
                .formLogin().disable()
                     .authorizeRequests()
                     .antMatchers("/admins/**")
                     .access("hasRole('ROLE_ADMIN')")
                .anyRequest()
                .permitAll()
                .and()
                     .logout().logoutSuccessUrl("/login")
                     .and()
                     .oauth2Login()
                     .userInfoEndpoint() // OAuth2 로그인 성공 후 가져올 설정들
                     .userService(customOAuth2UserService); // 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시
                     return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }







}
