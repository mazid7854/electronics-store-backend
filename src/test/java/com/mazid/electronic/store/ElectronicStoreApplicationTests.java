package com.mazid.electronic.store;

import com.mazid.electronic.store.entities.User;
import com.mazid.electronic.store.repositories.UserRepository;
import com.mazid.electronic.store.security.Jwt;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElectronicStoreApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Jwt jwt;

	@Test
	void contextLoads() {
	}


	@Test
	void testToken(){
		User user = userRepository.findByEmail("mazid@gmail.com").get();

		String token = jwt.generateToken(user);
		System.out.println("Token is :- "+token);

		String username = jwt.getUsernameFromToken(token);
		System.out.println("Username is :- "+username);





		System.out.println("Is token expired");
		System.out.println(jwt.isTokenExpired(token));
	}

}
