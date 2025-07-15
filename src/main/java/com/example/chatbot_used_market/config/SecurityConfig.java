package com.example.chatbot_used_market.config;

import com.example.chatbot_used_market.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final CustomOAuth2UserService customOAuth2UserService;

  public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
    this.customOAuth2UserService = customOAuth2UserService;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/", "/login", "/signup", "/css/**", "/js/**", "/images/**", "/users/**", "/reviews/**").permitAll()
                    .anyRequest().authenticated()  // 나머지는 로그인 필요
            )
            .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")  // 커스텀 로그인 페이지
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(customOAuth2UserService)  // 커스텀 OAuth2 서비스 사용
                    )
                    .defaultSuccessUrl("/courses", true)  // 로그인 성공 시 이동할 페이지
                    .failureUrl("/login?error=oauth2_error")  // 로그인 실패 시
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.disable())
            );

    return http.build();
  }
}
