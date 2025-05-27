package com.ticketmicroservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ticketmicroservice.domain.Employee;
import com.ticketmicroservice.domain.RoleName;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e JOIN e.roles r WHERE r.roleName = :roleName")
    List<Employee> findByRoleName(@Param("roleName") RoleName roleName);
}
