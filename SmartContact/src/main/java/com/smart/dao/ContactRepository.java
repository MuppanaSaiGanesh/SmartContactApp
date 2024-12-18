package com.smart.dao;

import org.springframework.data.repository.CrudRepository;

import com.smart.entity.Contact;

public interface ContactRepository extends CrudRepository<Contact, Integer>{

}
