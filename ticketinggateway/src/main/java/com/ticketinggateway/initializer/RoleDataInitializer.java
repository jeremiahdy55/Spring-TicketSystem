package com.ticketinggateway.initializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.repository.RoleRepository;

@Component
public class RoleDataInitializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(RoleName.ADMIN));
            roleRepository.save(new Role(RoleName.MANAGER));
            roleRepository.save(new Role(RoleName.USER));
        }
    }
}