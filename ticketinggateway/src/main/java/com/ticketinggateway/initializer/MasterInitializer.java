package com.ticketinggateway.initializer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        roleDataInitializer.init();
        employeeDataInitializer.init();
    }
}