package com.example.backend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BakongPaymentCheckRequestDTO {
    @NotBlank(message = "MD5 hash is required")
    @Size(min = 32, max = 32, message = "Invalid MD5 hash format")
    private String md5;
}
