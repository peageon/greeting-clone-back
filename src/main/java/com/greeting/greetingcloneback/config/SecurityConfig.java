package com.greeting.greetingcloneback.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.greeting.greetingcloneback.config.filter.JsonUsernamePasswordAuthenticationFilter;
import com.greeting.greetingcloneback.service.JpaUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JpaUserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig ->
                        csrfConfig.disable())
                )
                .httpBasic(httpConfig -> httpConfig.disable())
                .formLogin(formLoginConfig -> formLoginConfig.disable())
                .headers(headerConfig ->
                        headerConfig.frameOptions((frameOptionsConfig ->
                                frameOptionsConfig.disable())
                        )
                )
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/user/login/**", "/user/signup/**", "/error/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .securityContext(security -> security.securityContextRepository(new HttpSessionSecurityContextRepository()))
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
//                        .logoutSuccessUrl("/user/login")  // 이거 없어도 /user/login 으로 잘 보내지는듯
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter =
                new JsonUsernamePasswordAuthenticationFilter(objectMapper, loginSuccessHandler, loginFailureHandler);
        jsonUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager());
        return jsonUsernamePasswordAuthenticationFilter;
    }

    @Bean
    public OncePerRequestFilter authenticationStatusFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    System.out.println(auth.toString());
                    System.out.println("User " + auth.getName() + " is authenticated.");
                } else {
                    System.out.println("User is not authenticated.");
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return new ProviderManager(provider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

//Security 6.1.0부터는 메서드 체이닝의 사용을 지양하고 람다식을 통해 함수형으로 설정하게 지향하고 있습니다
//Example security config from the web
//@Bean