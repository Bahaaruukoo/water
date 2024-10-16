package com.UserManagement.account.Controller;

import com.UserManagement.account.Dto.PasswordChangeForm;
import com.UserManagement.account.Dto.UserDto;
import com.UserManagement.account.Dto.UserEditDto;
import com.UserManagement.account.Entity.Role;
import com.UserManagement.account.Entity.User;
import com.UserManagement.account.Repository.RoleRepository;
import com.UserManagement.account.service.UserService;

import jakarta.validation.Valid;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginForm() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getPrincipal())) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			User user = userService.findUserByEmail(userDetails.getUsername());
			if(user.isFirstLogin()){
				return "redirect:/change-password";
			}
			return "redirect:/home";
		}
		return "login_";
	}

	@GetMapping("/users/register")
	public String showRegistrationForm(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDto user = new UserDto();
		model.addAttribute("user", user);
		if (authentication != null
				&& authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getPrincipal())) {
			return "redirect:/users";
		} else {

			return "register";
		}
	}

	@PostMapping("/users/register/save")
	public String registration(
			@Valid @ModelAttribute("user") UserDto userDto,
			BindingResult result,
			Model model) {

		User existingUser = userService.findUserByEmail(userDto.getEmail());

		if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
			result.rejectValue("email", null,
					"There is already an account registered with the same email");
		}

		if (!userDto.getPassword().isEmpty()) {
			if (userDto.getPassword().length() < 7) {
				result.rejectValue("password", "field.min.length", "Password should have at least 7 characters");
			}
		}else{
			result.rejectValue("password", "field.min.length", "Password should not be empty.");
		}

		if (result.hasErrors()) {
			model.addAttribute("user", userDto);
			return "register";
		}

		userService.saveUser(userDto);
		return "redirect:/register?success=true";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/users/new/save")
	public String addUser(
			@Valid @ModelAttribute("user") UserDto userDto,
			BindingResult result,
			Model model) {
		User existingUser = userService.findUserByEmail(userDto.getEmail());

		if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
			result.rejectValue("email", null,
					"There is already an account registered with the same email");
		}

		if (!userDto.getPassword().isEmpty()) {
			if (userDto.getPassword().length() < 8) {
				result.rejectValue("password", "field.min.length", "Password should have at least 8 characters");
			}
		}else{
			result.rejectValue("password", "field.min.length", "Password should not be empty.");
		}

		if (result.hasErrors()) {
			model.addAttribute("user", userDto);
			List<Role> allRoles = roleRepository.findAll();
			model.addAttribute("allRoles", allRoles);
			model.addAttribute("content", "user/add"); // Reference to a Thymeleaf fragment

			return "admins";
		}

		userService.saveUser(userDto);
		return "redirect:/users?success=true";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users/new")
	public String showUserAddForm(Model model) {
		UserDto user = new UserDto();
		model.addAttribute("user", user);// Get all roles from the database
		List<Role> allRoles = roleRepository.findAll();  // Assuming you have a roleService
		model.addAttribute("allRoles", allRoles);
		model.addAttribute("content", "user/add"); // Reference to a Thymeleaf fragment

		return "admins";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users")
	public String users(Model model) {
		List<UserDto> users = userService.findAllUsers();

		model.addAttribute("users", users);
		model.addAttribute("content", "user/user"); // Reference to a Thymeleaf fragment

		return "admins";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/edit/{id}")
	public String editUser(
			@PathVariable Long id,
			Model model) {
		UserDto user = userService.findUserById(id);
		model.addAttribute("user", user);
		List<Role> allRoles = roleRepository.findAll();
		model.addAttribute("userRoles", user.getRoles());
		model.addAttribute("allRoles", allRoles); // Add roles to model
		model.addAttribute("content", "user/edit"); // Reference to a Thymeleaf fragment
		return "admins";
	}
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/edit/{id}")
	public String updateUserById(
			@Valid @ModelAttribute("user") UserEditDto updatedUserDto,
			//@Valid @ModelAttribute("allRoles") List<Role> roles,
			BindingResult result,
			@PathVariable Long id,
			Model model) {
		/*
		if (!updatedUserDto.getPassword().isEmpty()) {
			if (updatedUserDto.getPassword().length() < 7) {
				result.rejectValue("password", "field.min.length", "Password should have at least 7 characters");
			}
		} */
		if (result.hasErrors()) {
			model.addAttribute("user", updatedUserDto);
			List<Role> allRoles = roleRepository.findAll();
			model.addAttribute("allRoles", allRoles); // Add roles to model
			model.addAttribute("content", "user/edit"); // Reference to a Thymeleaf fragment
			return "admins";
		}

		userService.editUser(updatedUserDto, id);
		return "redirect:/users";
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("users/delete/{id}")
	public String deleteUser(
			@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "success", required = false) String success,
			RedirectAttributes redirectAttributes,
			@PathVariable Long id,
			Principal principal,
			Model model) {

		String loggedInUsername = principal.getName();

		User loggedInUser = userService.findUserByEmail(loggedInUsername);

		if (loggedInUser != null && loggedInUser.getId().equals(id)) {
			if (error != null) {
				redirectAttributes.addFlashAttribute("error", "You cannot delete yourself.");
			}
		} else {
			if (userService.doesUserExist(id)) {
				userService.deleteUserById(id);
				if (success != null) {
					redirectAttributes.addFlashAttribute("success", "User has been deleted successfully");
				}
			} else {
				if (error != null) {
					redirectAttributes.addFlashAttribute("error", "User does not exist");
				}
			}
		}
		return "redirect:/users";
	}


	@GetMapping("/change-password")
	public String showChangePasswordForm(Model model) {
		model.addAttribute("passwordChangeForm", new PasswordChangeForm());
		model.addAttribute("content", "user/change-password"); // Reference to a Thymeleaf fragment
		return "admins";
	}

	@PostMapping("/change-password")
	public String changePassword(PasswordChangeForm form, Principal principal) {
		// Get the current logged-in user
		User user = userService.findUserByEmail(principal.getName());
		// Change the password
		user.setPassword(passwordEncoder.encode(form.getNewPassword()));
		user.setFirstLogin(false);  // Mark that the user has changed their password

		userService.save(user);

		return "redirect:/user/profile";
	}

}
