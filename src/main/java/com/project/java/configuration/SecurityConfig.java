package com.project.java.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import persistences.repositories.UserRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final UserRepository userRepository;

	@Bean
	public UserDetailsService userDetailService() {
		return userName -> userRepository.findByUserName(userName).get();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticateProvider = new DaoAuthenticationProvider();
		authenticateProvider.setUserDetailsService(userDetailService());
		authenticateProvider.setPasswordEncoder(passwordEncoder());
		return authenticateProvider;

	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	public SecurityConfig(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}
}
