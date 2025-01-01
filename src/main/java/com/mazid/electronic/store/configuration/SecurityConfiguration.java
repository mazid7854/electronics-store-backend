package com.mazid.electronic.store.configuration;

import com.mazid.electronic.store.security.JwtAuthenticationEntryPoint;
import com.mazid.electronic.store.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    private JwtAuthenticationFilter filter;

    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;

    private final String[] PUBLIC_URLS= {
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",

    };

    // SecurityFilterChain bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {


        //configurations


        security.csrf(AbstractHttpConfigurer::disable);
        security.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();

                        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                        corsConfiguration.setAllowedHeaders(List.of("*"));
                        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setMaxAge(3600L);

                        return corsConfiguration;
                    }
                }));
        security.authorizeHttpRequests(request -> {
            //users
            request.requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN");

            //products
            request.requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/products/**").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "USER")
                    .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "USER");

            //categories
            request.requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN");

            //orders
            request.requestMatchers("/orders/**").hasAnyRole("ADMIN", "USER");


            //cart
            request.requestMatchers("/cart/**").hasAnyRole("ADMIN", "USER");

            //auth
            request.requestMatchers(HttpMethod.POST, "/auth/generate-token", "/auth/google-login").permitAll();

            //swagger

           request.requestMatchers( "/swagger-ui/**","/webjars/**","/swagger-resources/**","/v3/api-docs/**","/docs/**").permitAll();

                    request.anyRequest().authenticated();
        }).httpBasic(Customizer.withDefaults());
        security.formLogin(Customizer.withDefaults());
        security.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // entry point exception handler
        security.exceptionHandling(exception -> exception
                .authenticationEntryPoint(entryPoint)
        );




        //filters
        security.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);



        return security.build();
    }

    // password encoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {

        return builder.getAuthenticationManager();
    }
}
