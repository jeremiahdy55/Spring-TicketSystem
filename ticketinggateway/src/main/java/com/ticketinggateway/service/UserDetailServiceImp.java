package com.ticketinggateway.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.Employee;

@Service
public class UserDetailServiceImp implements UserDetailsService {

	@Autowired
	EmployeeService employeeService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Employee employee = employeeService.findByName(username);
		if(employee == null) {
			throw new UsernameNotFoundException(username);
		}
		Set<GrantedAuthority> ga = new HashSet<>();
		Set<Role> roles = employee.getRoles();
		for (Role role : roles) {
			System.out.println("UserDetailService.java: role.getRoleName()" + role.getRoleName());
			ga.add(new SimpleGrantedAuthority(role.getRoleName().name()));
		}

		return new org.springframework.security.core.userdetails.User(employee.getName(), employee.getPassword(), ga);
	}

}
