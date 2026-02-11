package com.example.backend.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BakongKhqrRequestDTO {
    @NotBlank(message = "Bakong Account ID is required")
    private String bakongAccountId;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "USD|KHR", message = "Currency must be either USD or KHR")
    private String currency;

    @NotBlank(message = "Bill number is required")
    private String billNumber;

    public String getBakongAccountId() { return bakongAccountId; }
    public void setBakongAccountId(String bakongAccountId) { this.bakongAccountId = bakongAccountId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }
}
