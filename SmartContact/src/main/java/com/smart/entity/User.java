package com.smart.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;

@Entity
public class User {

	@Id
	private int id;
	private String name;
	
	@Column(unique = true)
	private String email;
	private String password;
	private String role;
	
	@Lob
	private byte[] imageData;
	@Column(length = 500)
	private String about;
	
	@Transient
	private MultipartFile file;
	
	@PrePersist
    private void generateId() {
        if (this.id == 0) {
            this.id = generateUniqueId();
        }
    }

    private int generateUniqueId() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // Generates a number between 1000 and 9999
    }
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Contact> contacts=new ArrayList<>();
    
    
	
	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public byte[] getImageData() {
		return imageData;
	}
	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public User(String name, String email, String password, byte[] imageData, String about) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.imageData = imageData;
		this.about = about;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", about=" + about + "]";
	}
	
	
}
