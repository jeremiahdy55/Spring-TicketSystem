package com.ticketinggateway.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ticketinggateway.domain.Role;
import com.ticketinggateway.domain.RoleName;
import com.ticketinggateway.domain.User;
import com.ticketinggateway.repository.RoleRepository;
import com.ticketinggateway.repository.UserRepository;

@Service
public class UserService implements UserServiceInterface {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	public List<User> findAll() {
		return userRepository.findAll();
	}

	public User save(User u, RoleName roleName) {
		long roleId = roleRepository.findIdByRoleName(roleName);
		HashSet<Role> roleSet = new HashSet<>();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(u.getUserPassword());
		u.setUserPassword(hashedPassword);
		Role userRole = roleRepository.findById(roleId).orElse(null); //default is user:3
		roleSet.add(userRole);
		u.setRoles(roleSet);
		User user = userRepository.save(u);
		return user;
	}


	public User findByUserId(long uId) {
		Optional<User> u = userRepository.findById(uId);
		if(u.isPresent()) {
			return u.get();
		} else
		return null;
	}

	public void deleteUserById(long uId) {
		userRepository.deleteById(uId);		
	}

	public User findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

}
