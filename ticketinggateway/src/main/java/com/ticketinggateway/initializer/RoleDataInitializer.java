package com.ticketinggateway.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.repository.RoleRepository;


// This component will create the roles for the Employees
@Component
public class RoleDataInitializer {

    @Autowired
    RoleRepository roleRepository;

    public void init() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(RoleName.ADMIN));
            roleRepository.save(new Role(RoleName.MANAGER));
            roleRepository.save(new Role(RoleName.USER));
        }
    }
}