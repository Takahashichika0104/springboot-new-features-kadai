package com.example.samuraitravel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests

                // =========================
                // ★ 誰でもアクセス可能
                // =========================
                .requestMatchers(
                    "/css/**",
                    "/images/**",
                    "/js/**",
                    "/storage/**",
                    "/",
                    "/signup/**",
                    "/houses",
                    "/houses/*",
                    "/houses/*/reviews",     // ★ 追加：レビュー一覧
                    "/stripe/webhook"
                ).permitAll()

                // =========================
                // ★ ログイン必須
                // =========================
                .requestMatchers("/houses/*/reviews/new").authenticated()

                // =========================
                // ★ 管理者専用
                // =========================
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // =========================
                // ★ その他
                // =========================
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/?loggedIn")
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/?loggedOut")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/stripe/webhook")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
