package com.bmsapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmsapp.entity.User;
import com.bmsapp.exceptions.UserNotCreatedException;
import com.bmsapp.service.UserService;



@RestController
@RequestMapping("/user")
public class UserController {

	
	@Autowired 
	UserService userService;
	
	@PostMapping("/create")
	public ResponseEntity<String> createUser(@RequestBody User user) throws UserNotCreatedException {
		return new ResponseEntity<String>(userService.createUser(user), HttpStatus.CREATED);

	}
}
