package com.ticketinggateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.domain.Employee;

// interface defined here to "declare" certain methods and parameters

@Service
public interface EmployeeServiceInterface {

	public List<Employee> findAll();
	public Employee save(Employee u, RoleName roleName);
	public void deleteById(long id);
	public Employee findById(long id);
	public Employee findByName(String name);
}