package com.project.java.service;

import org.springframework.stereotype.Service;

import com.project.java.dto.user.UserDTO;
import com.project.java.persistences.entities.User;



@Service
public interface UserService {
	User createUser(UserDTO userDTO) throws Exception;
}
