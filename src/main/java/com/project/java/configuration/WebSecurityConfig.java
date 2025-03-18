package com.project.java.configuration;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebMvc
public class WebSecurityConfig {
	private final JwtFilterConfig jwtFilterConfig;
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	public WebSecurityConfig(JwtFilterConfig jwtFilterConfig) {
		super();
		this.jwtFilterConfig = jwtFilterConfig;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable)
				.addFilterBefore(jwtFilterConfig, UsernamePasswordAuthenticationFilter.class).authorizeHttpRequests(
						authorize -> authorize
						.requestMatchers(
								 "/auth/users/register", 
								 "/auth/users/login")
						.permitAll()
						.requestMatchers("auth/users/getuser").hasAnyAuthority("USER")
						.anyRequest().authenticated());



		http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
			@Override
			public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
				CorsConfiguration configuration = new CorsConfiguration();
				configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
				configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
				configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
				configuration.setExposedHeaders(List.of("x-auth-token"));
				configuration.setAllowCredentials(true);
				UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
				source.registerCorsConfiguration("/**", configuration);
				httpSecurityCorsConfigurer.configurationSource(source);
			}
		});

		return http.build();
}
}
