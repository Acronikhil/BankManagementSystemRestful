package com.bmsapp.service;

import com.bmsapp.entity.User;
import com.bmsapp.exceptions.UserNotCreatedException;

public interface UserService {

	// creating a new user
		public String createUser(User user) throws UserNotCreatedException;
}
