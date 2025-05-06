package com.ticketinggateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.domain.User;

// interface defined here to "declare" certain methods and parameters

@Service
public interface UserServiceInterface {

	public List<User> findAll();
	public User save(User u, RoleName roleName);
	public void deleteUserById(long uId);
	public User findByUserId(long uId);
	public User findByUserName(String userName);
}