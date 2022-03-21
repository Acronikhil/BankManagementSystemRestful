package com.bmsapp.service;

import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bmsapp.entity.User;
import com.bmsapp.exceptions.UserNotCreatedException;
import com.bmsapp.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public String createUser(User user) throws UserNotCreatedException {
		
		if (userRepository.existsById(user.getUserId())) {
			throw new UserNotCreatedException("Error creating user!\nUser already exist.");
		} else if (user.getName().equals("string") || user.getAddress().equals("string") || user.getContactNo() == 0
				|| user.getGender().equals("string") || user.getPin() == 0 || user == null) {
			throw new UserNotCreatedException("Error creating user!\nPlease check details.");

		} else {
			User u = userRepository.save(user);
			return "User created Successfully/nDetails:\n" + user;
		}
	}

}
