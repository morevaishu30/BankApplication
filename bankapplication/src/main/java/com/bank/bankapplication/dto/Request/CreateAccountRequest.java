package com.bank.bankapplication.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CreateAccountRequest {
    @NotBlank(message = "Account type is required")
    private String accountType;

    @DecimalMin(
            value = "0.00",
            message = "Initial deposit cannot be negative"
    )
    @DecimalMax(
            value = "1000000.00",
            message = "Initial deposit cannot exceed 1000000"
    )
    private BigDecimal initialDeposit;


}
