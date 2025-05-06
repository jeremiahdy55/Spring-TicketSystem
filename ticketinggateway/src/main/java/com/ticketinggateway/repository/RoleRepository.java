package com.ticketinggateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    @Query("SELECT r.id FROM Role r WHERE r.roleName = :roleName")
    Long findIdByRoleName(@Param("roleName") RoleName roleName);
}
