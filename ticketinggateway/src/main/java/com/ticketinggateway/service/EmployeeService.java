package com.ticketinggateway.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.domain.Employee;
import com.ticketinggateway.repository.RoleRepository;
import com.ticketinggateway.repository.EmployeeRepository;

@Service
public class EmployeeService implements EmployeeServiceInterface {

	@Autowired
	EmployeeRepository employeeRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	public List<Employee> findAll() {
		return employeeRepository.findAll();
	}

	public Employee save(Employee e, RoleName roleName) {
		long roleId = roleRepository.findIdByRoleName(roleName);
		HashSet<Role> roleSet = new HashSet<>();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(e.getPassword());
		e.setPassword(hashedPassword);
		Role userRole = roleRepository.findById(roleId).orElse(null);
		roleSet.add(userRole);
		e.setRoles(roleSet);
		Employee user = employeeRepository.save(e);
		return user;
	}

	public Employee findById(long id) {
		Optional<Employee> u = employeeRepository.findById(id);
		if(u.isPresent()) {
			return u.get();
		} else
		return null;
	}

	public boolean existsById(long id) {
		return employeeRepository.existsById(id);
	}

	public boolean isManager(long id) {
		Employee e = findById(id);
		boolean return_val = false;
		if (e != null) {
			for (Role r : e.getRoles()) {
				return_val = return_val || r.getRoleName() == RoleName.MANAGER;
			}
		}
		System.out.println(return_val);
		return return_val;
	}

	public void deleteById(long id) {
		employeeRepository.deleteById(id);		
	}

	public Employee findByName(String employeeName) {
		return employeeRepository.findByName(employeeName);
	}

}
