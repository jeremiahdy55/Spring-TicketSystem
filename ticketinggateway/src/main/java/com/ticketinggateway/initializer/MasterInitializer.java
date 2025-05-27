package com.ticketinggateway.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// This component ensure that the Roles table is initialized with the correct data
// and that a "master" account is created for full authorized access to any functionality
@Component
public class MasterInitializer implements CommandLineRunner {

    private final RoleDataInitializer roleDataInitializer;
    private final EmployeeDataInitializer employeeDataInitializer;

    public MasterInitializer(RoleDataInitializer firstInitializer, EmployeeDataInitializer secondInitializer) {
        this.roleDataInitializer = firstInitializer;
        this.employeeDataInitializer = secondInitializer;
    }

    @Override
    public void run(String... args) {
        // Initialize the data in sequence
        roleDataInitializer.init();
        employeeDataInitializer.init();
    }
}