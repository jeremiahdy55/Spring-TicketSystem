package com.ticketinggateway.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticketinggateway.domain.Employee;
import com.ticketinggateway.service.EmployeeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This Controller functions as the router to serve the webapp .jsp pages
@Controller
@SessionAttributes("user")
public class UserController {
	
	@Autowired 
	EmployeeService employeeService;

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

	@GetMapping("/landing")
	public String landing() {
		return "landing";
	}

	@GetMapping("/adminDashboard")
	public String adminDashboard(Model model, Principal principal) throws JsonProcessingException {
		return "adminDashboard";
	}

	@GetMapping("/managerDashboard")
	public String managerDashboard(Model model, Principal principal) throws JsonProcessingException {
		return "managerDashboard";
	}

	@GetMapping("/userDashboard")
	public String userDashboard(Model model, Principal principal) throws JsonProcessingException {
		return "userDashboard";
	}
	

	@GetMapping("/ticketForm")
	public String testPost(Model model, Principal principal) {
		return "ticketForm";
	}

	@GetMapping("/ticketDetails/{ticketId}")
	public String ticketDetails(@PathVariable Long ticketId, Model model, Principal principal) throws JsonProcessingException {
		return "ticketDetails";
	}

}
