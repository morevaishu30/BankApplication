package com.bank.bankapplication.service.Impl;

import com.bank.bankapplication.dto.Request.CreateAccountRequest;
import com.bank.bankapplication.dto.Response.BankAccountResponse;
import com.bank.bankapplication.entity.BankAccount;
import com.bank.bankapplication.service.BankAccountService;
import com.bank.bankapplication.entity.UserRegistration;
import com.bank.bankapplication.repository.BankAccountRepository;
import com.bank.bankapplication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
@Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BankAccountResponse createAccount(
            CreateAccountRequest request,
            String email
    ) {

        // Find logged-in customer
        UserRegistration user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Validate account type
        String accountType = request.getAccountType().toUpperCase();

        if (!accountType.equals("SAVINGS")
                && !accountType.equals("CURRENT")) {

            throw new RuntimeException(
                    "Account type must be SAVINGS or CURRENT"
            );
        }

        // Generate unique account number
        String accountNumber = generateAccountNumber();

        // Create account
        BankAccount bankAccount = BankAccount.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .balance(request.getInitialDeposit())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        // Save account
        BankAccount savedAccount =
                bankAccountRepository.save(bankAccount);

        // Return response
        return BankAccountResponse.builder()
                .message("Bank account created successfully")
                .accountId(savedAccount.getId())
                .accountNumber(savedAccount.getAccountNumber())
                .accountType(savedAccount.getAccountType())
                .balance(savedAccount.getBalance())
                .status(savedAccount.getStatus())
                .createdAt(savedAccount.getCreatedAt())
                .build();
    }

    private String generateAccountNumber() {

        Random random = new Random();

        String accountNumber;

        do {
            accountNumber = "1000"
                    + String.format(
                    "%08d",
                    random.nextInt(100000000)
            );

        } while (
                bankAccountRepository
                        .existsByAccountNumber(accountNumber)
        );

        return accountNumber;
    }


}
