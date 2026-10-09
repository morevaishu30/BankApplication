package com.bank.bankapplication.repository;
import com.bank.bankapplication.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>  {
    boolean existsByAccountNumber(String accountNumber);

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
