package com.project.java.configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.project.java.service.implement.IJwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import persistences.entities.User;
import persistences.repositories.UserRepository;

public class JwtFilterConfig extends OncePerRequestFilter{
	private static final Logger logger = LoggerFactory.getLogger(JwtFilterConfig.class);
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;
	private final IJwtService jwtService;
	
	private boolean isBypassToken(HttpServletRequest request) {
	    List<Pair<String, String>> listURLPass = Arrays.asList(
	        Pair.of("/auth/users/register", "POST")
	    );
	    String requestPath = request.getServletPath();
	    String requestMethod = request.getMethod();
	    logger.info("Checking bypass for Path: {}, Method: {}", requestPath, requestMethod);

	    for (Pair<String, String> bypassToken : listURLPass) {
	        if (requestPath.startsWith(bypassToken.getFirst()) && requestMethod.equals(bypassToken.getSecond())) {
	            return true;
	        }
	    }
	    return false;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		 logger.info("Request Path: {}", request.getServletPath());
			try {
				if(isBypassToken(request)) {
					logger.info("Bypassing token validation for URL: {}", request.getRequestURI());
					
				} else {
					String authHeader = request.getHeader("Authorization");
					logger.info("Required token validation for URL: {}", request.getRequestURI());
					if (authHeader == null || !authHeader.startsWith("Bearer")) {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
						return;
					}
					String token = authHeader.substring(7);
					String userName = jwtService.extractInfoByToken(token);
					if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
						logger.info("Validating token for user: {}", userName);
						User userDetails = (User) userDetailsService.loadUserByUsername(userName);
						logger.info("Validating token for userID: {}", userDetails.getId() );
						if (jwtService.validateAccessToken(token, userDetails)) {
							UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
									userDetails, null, userDetails.getAuthorities());
							logger.info("AuthenticationToken: {}", authenticationToken);
							authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
							SecurityContextHolder.getContext().setAuthentication(authenticationToken);
							Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
						    User user = (User) authentication.getPrincipal();
						    logger.info("User retrieved: {}", user.getUsername());
						}
					}
				}
				filterChain.doFilter(request,response);
			} catch (Exception e) {
				logger.info("error", e);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED");
			}
			
		
	}
	public JwtFilterConfig(UserDetailsService userDetailsService, UserRepository userRepository, IJwtService jwtService) {
		super();
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
		this.jwtService = jwtService;
	}

}
