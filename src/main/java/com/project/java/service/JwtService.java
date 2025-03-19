package com.project.java.service;

import com.project.java.persistences.entities.User;

public interface JwtService {
	String generateAccessToken(User user) throws Exception;

	String generateRefreshToken(User user) throws Exception;

	String extractInfoByToken(String accessToken) throws Exception;

	String extractRoleByToken(String accessToken) throws Exception;
	
	Boolean validateAccessToken(String accessToken, User user) throws Exception;
}
