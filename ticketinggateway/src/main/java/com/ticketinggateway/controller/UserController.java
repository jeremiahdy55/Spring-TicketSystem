package com.ticketinggateway.controller;

import java.security.Principal;

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
		@RequestParam String role, // TODO make it so I can delete this, need to implement manual role permission/deletion
		@RequestParam String managerId,
		@RequestParam String department,
		@RequestParam String project
		) {

		// Check that the managerId exists
		try {
			long manager_id = Long.valueOf(managerId);
		} catch (NumberFormatException e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("managerId is not an acceptable long value");
			return "signup";
		}
		long manager_id = Long.valueOf(managerId);
		if (!employeeService.existsById(manager_id)) {
			// If the Employee does not exist
			System.out.println("employee with managerId does not exist!");
			return "signup";
		} else if (!employeeService.isManager(manager_id)) {
			// If the Employee is not a RoleName.MANAGER
			System.out.println("employee with managerId is not a MANAGER!");
			return "signup";
		}

		// Create the Employee
		Employee user = new Employee();
		user.setName(userName);
		user.setEmail(userEmail);
		user.setPassword(password);
		user.setManagerId(manager_id);

		//TODO Change UI to allow
		user.setDepartment(department); // DROPDOWN MENU
		user.setProject(project); // DROPDOWN MENU

		employeeService.save(user, RoleName.valueOf(role)); //TODO change to default:USER
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

}
