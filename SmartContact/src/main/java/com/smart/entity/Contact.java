package com.smart.entity;

import java.util.Random;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.*;

@Entity
public class Contact {

	@Id
	private int cId;
	private String name;
	private String nickname;
	private String work;
	private String email;
	private String phone;
	@Lob
	private byte[] imageData;
	@Column(length = 50000)
	private String description;
	
	@Transient
	private MultipartFile file;
	
	@ManyToOne
	private User user;
	
	@PrePersist
    private void generateCId() {
        if (this.cId == 0) {
            this.cId = generateUniqueId();
        }
    }

    private int generateUniqueId() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // Generates a number between 1000 and 9999
    }

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}
	
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Contact(String name, String nickname, String work, String email, String phone, byte[] imageData,
			String description) {
		super();
		this.name = name;
		this.nickname = nickname;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.imageData = imageData;
		this.description = description;
	}

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Contact [cId=" + cId + ", name=" + name + ", nickname=" + nickname + ", work=" + work + ", email="
				+ email + ", phone=" + phone + ", description=" + description + "]";
	}
	
}
