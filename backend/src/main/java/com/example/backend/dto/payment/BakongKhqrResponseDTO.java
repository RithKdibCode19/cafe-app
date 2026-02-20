package com.example.backend.dto.payment;

import lombok.Data;

@Data
public class BakongKhqrResponseDTO {
    private boolean success;
    private KhqrData data;

    @Data
    public static class KhqrData {
        private String qr;
        private String md5;
        private String type;
        private String expiresAt;
    }
}
