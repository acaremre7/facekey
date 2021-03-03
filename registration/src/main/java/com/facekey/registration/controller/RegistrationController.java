package com.facekey.registration.controller;

import com.facekey.registration.jwt.JwtResponse;
import com.facekey.registration.jwt.JwtTokenUtil;
import com.facekey.registration.model.CredentialsDto;
import com.facekey.registration.model.UserDto;
import com.facekey.registration.service.FileStorageService;
import com.facekey.registration.service.UserService;
import com.facekey.registration.util.UserConverter;
import com.facekey.registration.util.UserValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.ValidationException;
import java.sql.SQLException;

@RestController
public class RegistrationController {

    private static final Logger logger = LogManager.getLogger(RegistrationController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/save")
    public ResponseEntity saveFace(@RequestParam("file") MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            logger.error("Uploaded file is not an image !");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Uploaded file is not an image !");
        }
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String fileName = fileStorageService.storeFile(file, auth.getName());
            logger.info("Image saved successfully: " + fileName);
            return ResponseEntity.status(HttpStatus.OK).body("Image saved successfully: " + fileName);
        } catch (Exception e) {
            logger.error("Error while saving image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving image: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity registerEmployee(@RequestBody UserDto userDto) {
        try {
            UserValidator.validateUser(userDto);
            userService.createUser(UserConverter.userDtoToUserEntity(userDto));
            return ResponseEntity.status(HttpStatus.OK).body("User registered successfully.");
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while registering user: " + e.getMessage());
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while registering user: " + e.getMessage());
        }
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody CredentialsDto credentialsDto) {
        try {
            UserValidator.validateCredentials(credentialsDto);
            authenticate(credentialsDto);
            final UserDetails userDetails = userService
                    .loadUserByUsername(credentialsDto.getUserName());
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while creating token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while logging in: " + e.getMessage());
        }
    }

    private void authenticate(CredentialsDto credentialsDto) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentialsDto.getUserName(), credentialsDto.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
