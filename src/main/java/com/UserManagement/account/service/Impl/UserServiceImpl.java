package com.UserManagement.account.service.Impl;

import com.UserManagement.account.Dto.AuthenticationRequest;
import com.UserManagement.account.Dto.UserEditDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.UserManagement.account.Dto.UserDto;
import com.UserManagement.account.Entity.Role;
import com.UserManagement.account.Entity.User;
import com.UserManagement.account.Repository.RoleRepository;
import com.UserManagement.account.Repository.UserRepository;
import com.UserManagement.account.service.UserService;

import jakarta.persistence.EntityNotFoundException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {

        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setAge(userDto.getAge());
        user.setPhone(userDto.getPhone());
        user.setGender(userDto.getGender());
        user.setAddress(userDto.getAddress());

        List<Role> roleList = new ArrayList<>();
        for (String roleName: userDto.getRoles()){
            roleList.add(roleRepository.findByName(roleName));
        }
        user.setRoles(roleList);
        userRepository.save(user);
    }

    public void deleteUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.ifPresent(user -> {
            user.getRoles().clear();
            userRepository.delete(user);
        });
    }

    public boolean doesUserExist(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.isPresent();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDto findUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            return mapToUserDto(userOptional.get());
        }
        return null;
    }

    public void editUser(UserEditDto updatedUserDto, Long userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        existingUser.setName(updatedUserDto.getFirstName() + " " + updatedUserDto.getLastName());
        existingUser.setAge(updatedUserDto.getAge());
        existingUser.setPhone(updatedUserDto.getPhone());
        existingUser.setGender(updatedUserDto.getGender());
        existingUser.setAddress(updatedUserDto.getAddress());

        List<Role> incomingRoles = new ArrayList<>();
        for (String role: updatedUserDto.getRoles()){
            incomingRoles.add(roleRepository.findById(Long.parseLong(role))
                    .orElseThrow(()->new RuntimeException("Role not found")));
        }
        /*
        List<Role> existingRoles = existingUser.getRoles();
        existingRoles.addAll(incomingRoles);
        existingUser.setRoles(existingRoles);
*/
        existingUser.setRoles(incomingRoles);
        userRepository.save(existingUser);
    }


    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        userDto.setAge(user.getAge());
        userDto.setPhone(user.getPhone());
        userDto.setGender(user.getGender());
        userDto.setAddress(user.getAddress());

        List<String> roleList = new ArrayList<>();
        //roleList.add(user.getRoles().get(0).getName());
        for (Role role : user.getRoles()){
            roleList.add(role.getName());
        }

        userDto.setRoles(roleList);
        return userDto;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    // Get currently authenticated user
    @Override
    public User getLoggedInUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username);
    }

    // Update user details
    @Override
    public void updateUser(User user) {
        userRepository.save(user);  // Save updated user
    }

    @Override
    public void saveAccountAdmin(AuthenticationRequest authenticationRequest) {

        User user = new User();
        user.setName("System Root Admin");
        user.setPhone("0000000000");
        user.setEmail(authenticationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authenticationRequest.getPassword()));
        user.setAddress("System");
        user.setAge(10);
        user.setGender("System");
        user.setFirstLogin(false);

        List<Role> roles = roleRepository.findAll();

        user.setRoles(roles);
        userRepository.save(user);

    }
}