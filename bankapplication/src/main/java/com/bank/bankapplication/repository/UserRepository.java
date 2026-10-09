package com.bank.bankapplication.repository;

import com.bank.bankapplication.entity.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<UserRegistration,Long > {
    boolean existsByEmail(String email);
    Optional<UserRegistration> findByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}
