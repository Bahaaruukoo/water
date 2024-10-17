package com.UserManagement.account.service;

import com.UserManagement.account.Dto.AuthenticationRequest;
import com.UserManagement.account.Dto.UserDto;
import com.UserManagement.account.Dto.UserEditDto;
import com.UserManagement.account.Entity.User;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
		
	void saveUser(UserDto userDto);

    List<UserDto> findAllUsers();

	User findUserByEmail(String email);

	UserDto findUserById(Long userId);

	boolean doesUserExist(Long userId);

	void editUser(UserEditDto updatedUserDto, Long userId);

	void deleteUserById(Long userId);

	User save(User user);

    User getLoggedInUser();

	void updateUser(User existingUser);

	void saveAccountAdmin( AuthenticationRequest authenticationRequest);
}
