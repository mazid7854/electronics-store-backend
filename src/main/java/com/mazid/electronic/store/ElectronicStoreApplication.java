package com.mazid.electronic.store;

import com.mazid.electronic.store.entities.Role;
import com.mazid.electronic.store.entities.User;
import com.mazid.electronic.store.repositories.RoleRepository;
import com.mazid.electronic.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableWebMvc
public class ElectronicStoreApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicStoreApplication.class, args);
	}

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Override
	public void run(String... args) throws Exception {

		Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElse(null);
		Role roleUser = roleRepository.findByName("ROLE_USER").orElse(null);
		if (roleAdmin == null) {
			Role role1 = new Role();
			role1.setRoleId(UUID.randomUUID().toString());
			role1.setName("ROLE_ADMIN");
			roleRepository.save(role1);
		}

		if (roleUser == null) {
			Role role2 = new Role();
			role2.setRoleId(UUID.randomUUID().toString());
			role2.setName("ROLE_USER");
			roleRepository.save(role2);
		}

		//create admin user
		if (userRepository.findByEmail("mazid@gmail.com").isEmpty()) {

			User user = new User();
			user.setUserId(UUID.randomUUID().toString());
			user.setName("Mazid");
			user.setEmail("mazid@gmail.com");
			user.setPassword(passwordEncoder.encode("mazid"));
			user.setGender("MALE");
			user.setAbout("I am Mazid, an admin user of this store");
			user.setImageName("mazid.png");
            assert roleUser != null;
            assert roleAdmin != null;
            user.setRoles(List.of(roleAdmin, roleUser));
			userRepository.save(user);
		}

	}
}
