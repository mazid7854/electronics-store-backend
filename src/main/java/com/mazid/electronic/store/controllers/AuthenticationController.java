package com.mazid.electronic.store.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mazid.electronic.store.dataTransferObjects.GoogleLoginRequest;
import com.mazid.electronic.store.dataTransferObjects.JwtRequest;
import com.mazid.electronic.store.dataTransferObjects.JwtResponse;
import com.mazid.electronic.store.dataTransferObjects.UserDto;
import com.mazid.electronic.store.entities.User;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.security.Jwt;
import com.mazid.electronic.store.services.CustomUserDetailService;
import com.mazid.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication APIs", description = "Login from google and jwt token generate")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Jwt jwt;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${google.client.id}")
    private String clientId;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationController.class);

    // method to generate token

    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        logger.info("Username: {}", request.getEmail());
        logger.info("Password: {}", request.getPassword());

        this.doAuthenticate(request.getEmail(), request.getPassword());

        User user = (User)userDetailsService.loadUserByUsername(request.getEmail());

        // generate token
        String token = jwt.generateToken(user);

        // return response
        JwtResponse jwtResponse = JwtResponse.builder().token(token).user(modelMapper.map(user, UserDto.class)).build();


        return ResponseEntity.ok(jwtResponse);

    }

    private void doAuthenticate(String username, String password) {

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authentication);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }


    @PostMapping("/google-login")
    public ResponseEntity<JwtResponse> handleGoogleLogin(@RequestBody GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        // Verify the Google ID Token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory())
                .setAudience(List.of(clientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(request.getIdToken());

        if (googleIdToken == null) {
            logger.error("Invalid ID token.");
            throw new BadCredentialsException("Invalid ID token.");
        }

        // Extract payload details
        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        // Fetch user by email
        User user;
        try {
            user = (User) customUserDetailService.loadUserByUsername(email);
        } catch (ResourceNotFoundException ex) {
            logger.info("User not found. Creating a new user with email: " + email);

            // Create a new user if not found
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setImageName(pictureUrl);
            user.setPassword(UUID.randomUUID().toString());
            user.setAbout("You are registered with a Google account, so you have to update your profile information.");

            // Save user to the database
            UserDto savedUser = userService.createUser(modelMapper.map(user, UserDto.class));
            user = modelMapper.map(savedUser, User.class);
        }

        // Generate JWT token
        String token = jwt.generateToken(user);

        return ResponseEntity.ok(
                JwtResponse.builder()
                        .token(token)
                        .user(modelMapper.map(user, UserDto.class))
                        .build()
        );
    }



}
