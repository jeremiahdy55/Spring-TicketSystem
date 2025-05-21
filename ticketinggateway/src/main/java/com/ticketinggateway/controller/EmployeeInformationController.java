package com.ticketinggateway.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.service.EmployeeService;

@Controller
public class EmployeeInformationController {

    @Autowired
    EmployeeService employeeService;

    @RequestMapping(value = "/getAdminEmployees", method = RequestMethod.GET)
    @ResponseBody
    public List<Long> getAdminEmployees() {
        List<Long> nums = employeeService.findByRoleName(RoleName.ADMIN);
        for (Long num : nums){
            System.out.println(num);
        }
        return employeeService.findByRoleName(RoleName.ADMIN);
    }

}
