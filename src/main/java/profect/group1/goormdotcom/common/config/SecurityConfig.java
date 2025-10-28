package profect.group1.goormdotcom.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import profect.group1.goormdotcom.common.security.AuthenticationFailedEntryPoint;
import profect.group1.goormdotcom.common.security.JwtAuthenticationFilter;
import profect.group1.goormdotcom.common.security.JwtTokenProvider;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationFailedEntryPoint authenticationFailedEntryPoint;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                // 익명사용자 비활성화
                .anonymous(anon -> anon.disable())
                // 세션기능 비활성화
//                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationFailedEntryPoint)
                )
                .authorizeHttpRequests(authz -> authz
                        // 스웨거 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**", "/api-docs", "/swagger-ui.html", "/h2-console/**").permitAll()
                        // 회원가입, 로그인 허용
                        .requestMatchers("/users/register").permitAll()
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/api/v1/delivery/address/mine/**").hasRole("CUSTOMER")
                        .requestMatchers("/api/v1/delivery/address/brand/**").hasRole("MASTER")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
