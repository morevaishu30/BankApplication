package com.bank.bankapplication.service.Impl;

import com.bank.bankapplication.config.JwtService;
import com.bank.bankapplication.dto.Request.LoginRequest;
import com.bank.bankapplication.dto.Request.LoginResponse;
import com.bank.bankapplication.dto.Request.RegistrationRequest;
import com.bank.bankapplication.entity.UserRegistration;
import com.bank.bankapplication.exception.EmailAlreadyRegisteredException;
import com.bank.bankapplication.exception.MobileNumberAlreadyRegisteredException;
import com.bank.bankapplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequest registrationRequest;
    private LoginRequest loginRequest;
    private UserRegistration user;

    @BeforeEach
    void setUp() {
        registrationRequest = RegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("Password@123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john.doe@example.com")
                .password("Password@123")
                .build();

        user = UserRegistration.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .mobileNumber("9876543210")
                .password("encodedPassword")
                .status("ACTIVE")
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void testRegister_Success() {
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByMobileNumber(registrationRequest.getMobileNumber())).thenReturn(false);
        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserRegistration.class))).thenReturn(user);

        String result = authenticationService.register(registrationRequest);

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, times(1)).existsByMobileNumber(registrationRequest.getMobileNumber());
        verify(passwordEncoder, times(1)).encode(registrationRequest.getPassword());
        verify(userRepository, times(1)).save(any(UserRegistration.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyRegisteredException when email already exists")
    void testRegister_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(true);

        EmailAlreadyRegisteredException exception = assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> authenticationService.register(registrationRequest)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, never()).existsByMobileNumber(anyString());
        verify(userRepository, never()).save(any(UserRegistration.class));
    }

    @Test
    @DisplayName("Should throw MobileNumberAlreadyRegisteredException when mobile number already exists")
    void testRegister_MobileNumberAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(registrationRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByMobileNumber(registrationRequest.getMobileNumber())).thenReturn(true);

        MobileNumberAlreadyRegisteredException exception = assertThrows(
                MobileNumberAlreadyRegisteredException.class,
                () -> authenticationService.register(registrationRequest)
        );

        assertEquals("Mobile number already registered", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail(registrationRequest.getEmail());
        verify(userRepository, times(1)).existsByMobileNumber(registrationRequest.getMobileNumber());
        verify(userRepository, never()).save(any(UserRegistration.class));
    }

    @Test
    @DisplayName("Should successfully login user with valid credentials")
    void testLogin_Success() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail(), user.getRole())).thenReturn("mockJwtToken");

        LoginResponse response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals(1L, response.getUserId());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals("mockJwtToken", response.getToken());

        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user.getEmail(), user.getRole());
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found during login")
    void testLogin_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authenticationService.login(loginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw RuntimeException when password does not match during login")
    void testLogin_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authenticationService.login(loginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}
