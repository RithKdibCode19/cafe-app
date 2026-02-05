package com.example.backend.dto;

import lombok.Data;

@Data
public class MobileCheckInRequestDTO {
    private String token;
    private Double latitude;
    private Double longitude;
}
