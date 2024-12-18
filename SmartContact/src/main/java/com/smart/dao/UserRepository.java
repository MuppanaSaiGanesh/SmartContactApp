package com.smart.dao;

import org.springframework.data.repository.CrudRepository;

import com.smart.entity.User;

public interface UserRepository extends CrudRepository<User, Integer>{

	public User findByEmail(String email);
}
