package com.akiba.backend.config; // 본인의 패키지 경로에 맞게 수정하세요

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (개발 편의성)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청을 로그인 없이 허용!
                )
                .formLogin(form -> form.disable()) // 그 지긋지긋한 로그인 폼 끄기
                .httpBasic(basic -> basic.disable()); // 기본 인증 팝업 끄기

        return http.build();
    }
}