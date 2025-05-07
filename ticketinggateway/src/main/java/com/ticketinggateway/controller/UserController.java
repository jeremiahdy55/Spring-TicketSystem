package com.ticketinggateway.controller;

import java.security.Principal;

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

	@GetMapping(value = "/login")
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
	public String signup(@RequestParam String userEmail, @RequestParam String userName, @RequestParam String password) {
		Employee user = new Employee();
		user.setName(userName);
		user.setEmail(userEmail);
		user.setPassword(password);

		//TODO Change UI to allow
		user.setDepartment("default department");
		user.setManagerId(null); // TODO also make a reference constraint that references employee.id
		user.setProject("default project");

		employeeService.save(user, RoleName.ADMIN); //TODO change to default:USER
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
