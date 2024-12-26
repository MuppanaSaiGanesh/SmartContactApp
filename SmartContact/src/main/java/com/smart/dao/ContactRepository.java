package com.smart.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.smart.entity.Contact;
import com.smart.entity.User;

public interface ContactRepository extends CrudRepository<Contact, Integer>{

	public List<Contact> findByNameContainingAndUser(String name, User user);
}
