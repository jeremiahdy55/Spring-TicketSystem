package com.ticketinggateway.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.domain.Employee;
import com.ticketinggateway.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
@SessionAttributes("user")
public class UserController {
	
	@Autowired EmployeeService employeeService;

	@GetMapping(value = {"/login", "/"})
	public String login(@RequestParam(required = false) String logout, @RequestParam(required = false) String error,
			HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model) {
		String message = "";
		if (error != null) {
			message = "ERROR! Credentials";
		}
		if (logout != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
			}
			message = "Logout";
			return "login";
		}
		model.addAttribute("Message", message);
		return "login";

	}

	@PostMapping(value = "/signup")
	public String signup(
		@RequestParam String userEmail, 
		@RequestParam String userName, 
		@RequestParam String password,
		@RequestParam List<String> roles,
		@RequestParam String department,
		@RequestParam String project,
		@RequestParam(required=false) Long managerId
		) {
		if (managerId != null) {
			// Check that the managerId exists
			if (!employeeService.existsById(managerId)) {
				// If the Employee does not exist
				System.out.println("employee with managerId does not exist!");
				return "signup";
			} else if (!employeeService.isManager(managerId)) {
				// If the Employee is not a RoleName.MANAGER
				System.out.println("employee with managerId is not a MANAGER!");
				return "signup";
			}
		}

		// Create the Employee
		Employee user = new Employee();
		user.setName(userName);
		user.setEmail(userEmail);
		user.setPassword(password);
		user.setManagerId(managerId);
		user.setDepartment(department);
		user.setProject(project);
		employeeService.save(user, roles);
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		return "signup";
	}

	@GetMapping("/homePage")
	public String homePage() {
		return "homePage";
	}

	//TODO delete me later
	@GetMapping("/testUI")
	public String testGetTickets(Model model, Principal principal) {
		Employee thisUser = employeeService.findByName(principal.getName());
		model.addAttribute("userId", thisUser.getId());
		return "testUI";
	}

	@GetMapping("path")
	public String getMethodName(@RequestParam String param) {
		return new String();
	}
	

}
