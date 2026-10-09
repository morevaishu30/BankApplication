package com.bank.bankapplication.service.Impl;



import com.bank.bankapplication.config.JwtService;
import com.bank.bankapplication.dto.Request.LoginRequest;
import com.bank.bankapplication.dto.Request.LoginResponse;
import com.bank.bankapplication.dto.Request.RegistrationRequest;
import com.bank.bankapplication.entity.UserRegistration;
import com.bank.bankapplication.exception.EmailAlreadyRegisteredException;
import com.bank.bankapplication.exception.MobileNumberAlreadyRegisteredException;
import com.bank.bankapplication.repository.UserRepository;
import com.bank.bankapplication.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Autowired
    public AuthenticationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String register(RegistrationRequest registrationRequest) {

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        if (userRepository.existsByMobileNumber(registrationRequest.getMobileNumber())) {
            throw new MobileNumberAlreadyRegisteredException("Mobile number already registered");
        }

        String encodedPassword =
                passwordEncoder.encode(registrationRequest.getPassword());

        UserRegistration userRegistration = UserRegistration.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .mobileNumber(registrationRequest.getMobileNumber())
                .password(encodedPassword)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(userRegistration);

        return "User registered successfully";
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        UserRegistration user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return LoginResponse.builder()
                .message("Login successful")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .build();
        }
}