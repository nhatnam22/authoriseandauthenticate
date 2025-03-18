package com.project.java.service.implement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.project.java.service.JwtService;

import persistences.entities.Role;
import persistences.entities.User;


public class IJwtService implements JwtService{
	
	@Value("${jwt.secret-key}")
	private String secretKey;
	
	private static final Logger logger = LoggerFactory.getLogger(IJwtService.class);

	@Override
	public String generateAccessToken(User user) throws Exception {
		Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
		Map<String, Object> claim = new HashMap<>();
		Set<Role> roles = user.getRoles();
	    List<String> roleNames = new ArrayList<>();
	    for (Role role : roles) { 
	        roleNames.add(role.getRoleName().toString()); 
	    }
		claim.put("user_id",user.getId());
		claim.put("roles", roleNames );
		try {
			String accessToken = JWT.create().withPayload(claim).withSubject(user.getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)).sign(algorithm);

			return accessToken;
		} catch (Exception exp) {
			throw new Exception("khong co token");
		}
	}

	@Override
	public String generateRefreshToken(User user) throws Exception {
		Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
		try {
			String refreshToken = JWT.create().withClaim("user_id", user.getId()).withSubject(user.getUsername())
					.withExpiresAt(new Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L)).sign(algorithm);

			return refreshToken;
		} catch (Exception exp) {
			throw new Exception("khong co token");
		}
	}

	@Override
	public String extractInfoByToken(String accessToken) throws Exception {
		DecodedJWT decodeJWT = JWT.decode(accessToken);
		return decodeJWT.getSubject();
	}

	@Override
	public String extractRoleByToken(String accessToken) throws Exception {
		DecodedJWT decodeJWT = JWT.decode(accessToken);
		Map<String,Object> roles = new HashMap<>();
		try {
			decodeJWT.getClaims().forEach((key, value)-> {
				if(key.equals("roles")) {
					roles.put(key, value);
				}
			});
		} catch(Exception e) {
			throw new Exception("khong co role");
		}
		return null;
	}

	@Override
	public Boolean validateAccessToken(String accessToken, User user) throws Exception {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
			JWTVerifier jwtVerify = JWT.require(algorithm).withSubject(user.getUsername()).build();
			DecodedJWT decodedJWT = jwtVerify.verify(accessToken);
			Integer userIdFromToken = decodedJWT.getClaim("user_id").asInt();
			if (userIdFromToken != null) {
			    logger.info("User ID from token as integer: {}", userIdFromToken);
			} else {
			    logger.error("Failed to decode user_id as integer");
			}
			Integer userIdFromUserDetails = user.getId().intValue();
			if (!userIdFromUserDetails.equals(userIdFromToken)) {
				logger.error("User ID mismatch: {} != {}", user.getId(), decodedJWT.getClaim("user_id").asString());
				return false;
			}
			List<String> rolesFromToken = decodedJWT.getClaim("roles").asList(String.class);
			Set<Role> roles = user.getRoles();
			List<String> userRoles = new ArrayList<>();
			roles.stream().forEach(role -> userRoles.add(role.getRoleName().toString()));
			if (!rolesFromToken.containsAll(userRoles)) {
				logger.error("Roles mismatch. Token roles: {}, User roles: {}", rolesFromToken, userRoles);
				return false;
			}
		} catch( Exception e) {
			throw new Exception("khong xac thuc token");
		}
		return true;
	}

}
