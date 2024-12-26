package com.smart.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private EmailService emailService;

	@GetMapping("/home")
	public String test() {
		return "home";
	}

	@RequestMapping("/register")
	public String register() {
		return "register";
	}

	@RequestMapping("/registerhandler")
	public String registerHandler(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("image") MultipartFile file,
			@RequestParam("about") String about, RedirectAttributes redirectAttributes) throws IOException {
		User user = new User(name, email, password, file.getBytes(), about);
		if (userRepo.save(user) != null) {
			// Send confirmation email
			String subject = "Registration Successful";
			String body = "Dear " + user.getName() + ",\n\nThank you for registering at SmartConnect!";
	        emailService.sendEmail(user.getEmail(), subject, body);
			redirectAttributes.addFlashAttribute("message", subject);
			
		} else {
			redirectAttributes.addFlashAttribute("message", "Registration failed!");
		}

		return "redirect:home";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@RequestMapping("/loginHandler")
	public String loginHandler(@RequestParam("email") String email, @RequestParam("password") String password,
	        RedirectAttributes redirectAttributes, HttpSession session) {

	    System.out.println("Email: " + email);  // Add logs for debugging
	    System.out.println("Password: " + password);
	    
	    User user = userRepo.findByEmail(email);
	    if (user != null && password.equals(user.getPassword())) {
	        redirectAttributes.addFlashAttribute("message", "Login successful..");
	        session.setAttribute("user", user);
	        return "redirect:/welcome"; // Redirect to the welcome page
	    }

	    redirectAttributes.addFlashAttribute("message", "Invalid Login Credentials..");
	    return "redirect:/login"; // Redirect back to the login page on failure
	}


	@RequestMapping("/welcome")
	public String welcome(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			byte[] imageData = user.getImageData();
			String image = Base64.getEncoder().encodeToString(imageData);
//	        session.setAttribute("image", image);
			model.addAttribute("image", image);
			model.addAttribute("id", user.getId());
		}
		return "welcome";
	}

	@RequestMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
		session.invalidate();
		redirectAttributes.addFlashAttribute("message", "Logged out Successfully!!");
		return "redirect:home";
	}

	@RequestMapping("/forgot-password")
	public String forgotPassword() {
		return "forgot-password";
	}

	@RequestMapping("/forgot-password-handler")
	public String forgotPasswordHandler(@RequestParam("email") String email, @RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		User user = userRepo.findByEmail(email);
		if (user != null) {
			user.setPassword(password);
			userRepo.save(user);
			redirectAttributes.addFlashAttribute("message", "Password Updated successfully!!\n you can login now");
			return "redirect:login";
		}
		redirectAttributes.addFlashAttribute("message", "Invalid Email!!\n  please provide valid email..");
		return "redirect:forgot-password";

	}

	@RequestMapping("/upload-contact/{id}")
	public String uploadContact(@PathVariable("id") int id, Model model) {
		model.addAttribute("id", id);
		return "upload-contact";
	}

	@RequestMapping("/submit-contact")
	public String submitContact(@RequestParam("id") int id, @RequestParam("name") String name,
			@RequestParam("nickname") String nickname, @RequestParam("email") String email,
			@RequestParam("work") String work, @RequestParam("phone") String phone,
			@RequestParam("image") MultipartFile file, @RequestParam("description") String description,
			RedirectAttributes redirectAttributes) throws IOException {

		Contact contact = new Contact(name, nickname, work, email, phone, file.getBytes(), description);

		Optional<User> optUser = userRepo.findById(id);
		if (!optUser.isEmpty()) {
			contact.setUser(optUser.get());
			optUser.get().getContacts().add(contact);
			contactRepo.save(contact);
			redirectAttributes.addFlashAttribute("message", "Contact added successfully..");
			return "redirect:welcome";
		}
		redirectAttributes.addFlashAttribute("message", "Contact not added..");
		return "uploadContact";
	}

	@RequestMapping("/show-contacts/{id}")
	public String showContacts(@PathVariable("id") int id, Model model) {
		Optional<User> optUser = userRepo.findById(id);
		User user = optUser.get();
		List<Contact> contacts = user.getContacts();
		model.addAttribute("contacts", contacts);
		model.addAttribute("id", id);
		return "show-contacts";
	}

	// Endpoint to fetch the contact image
	@GetMapping("/contact-image/{id}")
	public ResponseEntity<byte[]> getContactImage(@PathVariable("id") int id) throws IOException {
		Optional<Contact> contactOpt = contactRepo.findById(id);
		if (contactOpt.isPresent() && contactOpt.get().getImageData() != null) {
			byte[] imageData = contactOpt.get().getImageData(); // Byte array from DB
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "inline")
					.contentType(MediaType.IMAGE_JPEG) // Adjust for PNG if needed
					.body(imageData);
		} else {
			// Return a default image or error message if no image is found
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping("/view-contact/{cId}")
	public String viewContact(@PathVariable("cId") int cId, Model model) {
		Optional<Contact> optContact = contactRepo.findById(cId);
		model.addAttribute("contact", optContact.get());
		return "view-contact";
	}

	@RequestMapping("/edit-contact/{cId}")
	public String editContact(@PathVariable("cId") int cId, Model model) {
		Optional<Contact> optContact = contactRepo.findById(cId);
		model.addAttribute("contact", optContact.get());
		return "edit-contact";
	}
	
	

	@RequestMapping("/update-contact")
	public String updateContact(@RequestParam("cId") int cId, @RequestParam("name") String name,
			@RequestParam("nickname") String nickname, @RequestParam("email") String email,
			@RequestParam("work") String work, @RequestParam("phone") String phone,
			@RequestParam("image") MultipartFile file, @RequestParam("description") String description,
			RedirectAttributes redirectAttributes) throws IOException {

		// Fetch the existing contact
		Optional<Contact> optContact = contactRepo.findById(cId);
		if (optContact.isPresent()) {
			Contact contact = optContact.get();

			// Update fields with new values
			contact.setName(name);
			contact.setNickname(nickname);
			contact.setWork(work);
			contact.setEmail(email);
			contact.setPhone(phone);
			contact.setDescription(description);

			// If a new image is provided, update it
			if (!file.isEmpty()) {
				contact.setImageData(file.getBytes());
			}

			// Save the updated contact
			contactRepo.save(contact);
		} else {
			// Handle case where the contact ID is invalid
			redirectAttributes.addFlashAttribute("error", "Contact not found");
			return "redirect:/edit-contact/" + cId;
		}

		return "redirect:/view-contact/" + cId;
	}
	
	@RequestMapping("/view-profile/{id}")
	public String viewProfile(@PathVariable("id") int id, Model model) {
		Optional<User> optUser = userRepo.findById(id);
		byte[] imageData = optUser.get().getImageData();
		String image = Base64.getEncoder().encodeToString(imageData);
//        session.setAttribute("image", image);
		model.addAttribute("image", image);
		model.addAttribute("user", optUser.get());
		return "view-profile";
	}

	@RequestMapping("/edit-profile/{id}")
	public String editProfile(@PathVariable("id") int id, Model model) {
		Optional<User> optUser = userRepo.findById(id);
		model.addAttribute("user", optUser.get());
		return "edit-profile";
	}
	
	@RequestMapping("/update-profile")
	public String updateProfile(@RequestParam("id") int id, @RequestParam("name") String name,
			@RequestParam("email") String email,
			@RequestParam("image") MultipartFile file, @RequestParam("about") String about,
			RedirectAttributes redirectAttributes) throws IOException {

		// Fetch the existing contact
		Optional<User> optUser = userRepo.findById(id);
		if (optUser.isPresent()) {
			User user = optUser.get();

			// Update fields with new values
			user.setName(name);
			user.setEmail(email);
			user.setAbout(about);

			// If a new image is provided, update it
			if (!file.isEmpty()) {
				user.setImageData(file.getBytes());
			}

			// Save the updated contact
			userRepo.save(user);
		} else {
			// Handle case where the contact ID is invalid
			redirectAttributes.addFlashAttribute("message", "Contact not found");
			return "redirect:/edit-profile/" + id;
		}

		return "redirect:/view-profile/" + id;
	}

	@RequestMapping("/delete-contact/{cId}/{id}")
	public String deleteContact(@PathVariable("cId") int cId, @PathVariable("id") int id,
			RedirectAttributes redirectAttributes) {
		contactRepo.deleteById(cId);
		redirectAttributes.addFlashAttribute("message", "contact deleted successfully..");
		return "redirect:/show-contacts/" + id;
	}

	@RequestMapping("/delete-all/{id}")
	public String deleteAll(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		// Fetch the user and their contacts
		Optional<User> optionalUser = userRepo.findById(id);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			List<Contact> contacts = user.getContacts();

			// Clear the contacts from the user to avoid orphan references
			user.getContacts().clear();
			userRepo.save(user);

			// Now delete all contacts
			contactRepo.deleteAll(contacts);

			redirectAttributes.addFlashAttribute("message", "Cleared all the contacts.");
		} else {
			redirectAttributes.addFlashAttribute("error", "User not found.");
		}

		return "redirect:/show-contacts/" + id;
	}

}