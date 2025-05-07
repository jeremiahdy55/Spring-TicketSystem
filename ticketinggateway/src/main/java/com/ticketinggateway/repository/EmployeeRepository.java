package com.ticketinggateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticketinggateway.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
    Employee findByName(String employeeName);
}
