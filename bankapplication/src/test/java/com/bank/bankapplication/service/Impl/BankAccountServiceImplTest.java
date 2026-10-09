package com.bank.bankapplication.service.Impl;

import com.bank.bankapplication.dto.Request.CreateAccountRequest;
import com.bank.bankapplication.dto.Response.BankAccountResponse;
import com.bank.bankapplication.entity.BankAccount;
import com.bank.bankapplication.entity.UserRegistration;
import com.bank.bankapplication.repository.BankAccountRepository;
import com.bank.bankapplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private UserRegistration user;
    private CreateAccountRequest savingsRequest;
    private CreateAccountRequest currentRequest;
    private BankAccount savedBankAccount;

    @BeforeEach
    void setUp() {
        user = UserRegistration.builder()
                .id(1L)
                .firstName("Vaishu")
                .lastName("More")
                .email("vaishu@example.com")
                .mobileNumber("9876543210")
                .password("encodedPassword")
                .status("ACTIVE")
                .role("ROLE_USER")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        savingsRequest = new CreateAccountRequest();
        savingsRequest.setAccountType("SAVINGS");
        savingsRequest.setInitialDeposit(new BigDecimal("5000.00"));

        currentRequest = new CreateAccountRequest();
        currentRequest.setAccountType("current"); // testing case insensitivity
        currentRequest.setInitialDeposit(new BigDecimal("10000.00"));

        savedBankAccount = BankAccount.builder()
                .id(100L)
                .accountNumber("100012345678")
                .accountType("SAVINGS")
                .balance(new BigDecimal("5000.00"))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a SAVINGS bank account")
    void testCreateAccount_Savings_Success() {
        when(userRepository.findByEmail("vaishu@example.com")).thenReturn(Optional.of(user));
        when(bankAccountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savedBankAccount);

        BankAccountResponse response = bankAccountService.createAccount(savingsRequest, "vaishu@example.com");

        assertNotNull(response);
        assertEquals("Bank account created successfully", response.getMessage());
        assertEquals(100L, response.getAccountId());
        assertEquals("100012345678", response.getAccountNumber());
        assertEquals("SAVINGS", response.getAccountType());
        assertEquals(new BigDecimal("5000.00"), response.getBalance());
        assertEquals("ACTIVE", response.getStatus());
        assertNotNull(response.getCreatedAt());

        verify(userRepository, times(1)).findByEmail("vaishu@example.com");
        verify(bankAccountRepository, times(1)).existsByAccountNumber(anyString());
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("Should successfully create a CURRENT bank account with lowercase input")
    void testCreateAccount_Current_Success() {
        BankAccount currentAccount = BankAccount.builder()
                .id(101L)
                .accountNumber("100087654321")
                .accountType("CURRENT")
                .balance(new BigDecimal("10000.00"))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        when(userRepository.findByEmail("vaishu@example.com")).thenReturn(Optional.of(user));
        when(bankAccountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(currentAccount);

        BankAccountResponse response = bankAccountService.createAccount(currentRequest, "vaishu@example.com");

        assertNotNull(response);
        assertEquals("Bank account created successfully", response.getMessage());
        assertEquals("CURRENT", response.getAccountType());
        assertEquals(new BigDecimal("10000.00"), response.getBalance());

        verify(userRepository, times(1)).findByEmail("vaishu@example.com");
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when user is not found")
    void testCreateAccount_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankAccountService.createAccount(savingsRequest, "nonexistent@example.com")
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when account type is neither SAVINGS nor CURRENT")
    void testCreateAccount_InvalidAccountType_ThrowsException() {
        CreateAccountRequest invalidRequest = new CreateAccountRequest();
        invalidRequest.setAccountType("SALARY");
        invalidRequest.setInitialDeposit(new BigDecimal("1000.00"));

        when(userRepository.findByEmail("vaishu@example.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bankAccountService.createAccount(invalidRequest, "vaishu@example.com")
        );

        assertEquals("Account type must be SAVINGS or CURRENT", exception.getMessage());
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    @DisplayName("Should retry account number generation if collision occurs")
    void testCreateAccount_AccountNumberCollision_Retries() {
        when(userRepository.findByEmail("vaishu@example.com")).thenReturn(Optional.of(user));
        // First generated account number exists, second does not
        when(bankAccountRepository.existsByAccountNumber(anyString()))
                .thenReturn(true)
                .thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(savedBankAccount);

        BankAccountResponse response = bankAccountService.createAccount(savingsRequest, "vaishu@example.com");

        assertNotNull(response);
        // Verify existsByAccountNumber was called at least 2 times due to collision
        verify(bankAccountRepository, times(2)).existsByAccountNumber(anyString());
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
    }
}
