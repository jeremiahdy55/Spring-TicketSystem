package com.ticketinggateway.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketinggateway.domain.Employee;
import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.service.EmployeeService;

// This class will add global context for ModelAttribute, accessible by all .jsp files
@ControllerAdvice
public class GlobalModelAttributeAdvice {

    @Autowired
    EmployeeService employeeService;

    @ModelAttribute("userId")
    public long addUserId(Principal principal) {
        if (principal == null) return 0L;
        Employee thisUser = employeeService.findByName(principal.getName());
        return thisUser.getId();
    }

    @ModelAttribute("roles")
    public String addRoles(Principal principal) throws JsonProcessingException {
        if (principal == null) return null;
        Employee thisUser = employeeService.findByName(principal.getName());
        List<String> roleList = thisUser.getRoles()
                .stream()
                .map(Role::getRoleName)
                .map(RoleName::name)
                .collect(Collectors.toList());
        return new ObjectMapper().writeValueAsString(roleList);
    }
}