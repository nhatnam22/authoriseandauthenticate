package com.project.java.service.implement;

import com.project.java.dto.user.UserDTO;
import com.project.java.service.UserService;

import persistences.entities.User;
import persistences.repositories.UserRepository;

public class IUserService implements UserService{
	private final UserRepository userRepository;

	@Override
	public User createUser(UserDTO userDTO) throws Exception {
		String userName = userDTO.getUserName();
		if(userName == null) throw new Exception("nguoi dung khong co");
		String password = userDTO.getUserName().toLowerCase().trim().concat("123");
		User newUser = new User(userName, password);
		userRepository.save(newUser);
		return newUser;
	}

	public IUserService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

}
