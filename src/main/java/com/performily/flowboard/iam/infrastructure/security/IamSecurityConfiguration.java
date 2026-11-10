
package com.performily.flowboard.iam.infrastructure.security;

import com.performily.flowboard.iam.application.internal.commandservices.IamService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class IamSecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
                java.util.Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(v -> !v.isBlank()).toList()
        );

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(
                List.of("Authorization", "Content-Type", "Accept")
        );

        config.setExposedHeaders(
                List.of("Location")
        );

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, IamService iam)
            throws Exception {

        return http
                .csrf(c -> c.disable())
                .httpBasic(c -> c.disable())
                .formLogin(c -> c.disable())
                .exceptionHandling(e -> e
                    .authenticationEntryPoint((req, res, ex) -> res.sendError(401, "Authentication required"))
                    .accessDeniedHandler((req, res, ex) -> res.sendError(403, "Access denied")))

                .cors(Customizer.withDefaults())

                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/login"
                        ).permitAll()

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/health").permitAll()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(new OncePerRequestFilter() {

                    @Override
                    protected void doFilterInternal(
                            HttpServletRequest req,
                            HttpServletResponse res,
                            FilterChain chain
                    ) throws ServletException, IOException {

                        if (HttpMethod.OPTIONS.matches(req.getMethod())) {
                            chain.doFilter(req, res);
                            return;
                        }

                        // A public login must not depend on a stale token supplied by the client.
                        if (req.getRequestURI().equals("/api/v1/auth/login")
                                && HttpMethod.POST.matches(req.getMethod())) {
                            chain.doFilter(req, res);
                            return;
                        }

                        String h = req.getHeader("Authorization");

                        if (h != null && h.startsWith("Bearer ")) {

                            try {
                                var u = iam.valid(h.substring(7));

                                if (u.mustChangePassword
                                        && !req.getRequestURI().equals(
                                                "/api/v1/auth/change-password")
                                        && !req.getRequestURI().equals(
                                                "/api/v1/auth/logout")) {

                                    res.sendError(
                                            403,
                                            "Password change required"
                                    );
                                    return;
                                }

                                var auth =
                                        new UsernamePasswordAuthenticationToken(
                                                u,
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_" + u.role.name()
                                                        )
                                                )
                                        );

                                SecurityContextHolder.getContext()
                                        .setAuthentication(auth);

                            } catch (Exception e) {

                                SecurityContextHolder.clearContext();

                                res.sendError(401, "Invalid token");
                                return;
                            }
                        }

                        chain.doFilter(req, res);
                    }

                }, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
