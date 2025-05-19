package com.ticketinggateway.initializer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ticketinggateway.domain.Employee;
import com.ticketinggateway.repository.EmployeeRepository;
import com.ticketinggateway.service.EmployeeService;

@Component
public class EmployeeDataInitializer implements CommandLineRunner {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeService employeeService;

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            Employee user = new Employee();
            user.setName("master");
            user.setEmail("defaultemail");
            user.setPassword("master");
            user.setManagerId(null);
            user.setDepartment("default department");
            user.setProject("default project");
            employeeService.save(user, List.of("ADMIN", "MANAGER", "USER"));
        }
    }
}