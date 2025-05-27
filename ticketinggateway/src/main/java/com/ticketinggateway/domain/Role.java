package com.ticketinggateway.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

// This entity models the Role (authorizations) given to Employees
// NOTE: this entity is never accesible by USERs, ADMINs, or MANAGERs and is initialized at start
@Entity
public class Role {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long roleId;
	
	@Enumerated(EnumType.STRING)
	private RoleName roleName;
	
	@ManyToMany(mappedBy="roles", cascade=CascadeType.ALL)
	Set<Employee> employee = new HashSet<>();

	public Role (RoleName roleName) {
		this.roleName = roleName;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public Set<Employee> getEmployee() {
		return employee;
	}

	public RoleName getRoleName() {
		return roleName;
	}

	public void setRoleName(RoleName roleName) {
		this.roleName = roleName;
	}

	public void setEmployee(Set<Employee> employee) {
		this.employee = employee;
	}

	public Role() {
		super();
	}
	
}