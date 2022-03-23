package com.bmsrestfulapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;


import com.bmsrestfulapi.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	@Query("from User u Inner Join Login l on l.user.userId = u.userId where l.isVerified = 0")
    public List<User> getNotVerifiedUsers();
	
	@Query("from User u where u.pin=:pin and u.userId=:userId")
	public User getPin(@Param(value = "pin") Integer pin,@Param(value = "userId") Integer userId);
	
	@Query("from User u where u.pin=:pin")
	public User verifyPin(@Param(value = "pin") Integer pin);
	
	@Query("from User u where u.contactNo=:contactNo")
	public User existByContactNo(@Param(value= "contactNo") Long contactNo );
}
