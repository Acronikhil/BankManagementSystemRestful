package com.bmsapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmsapp.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
