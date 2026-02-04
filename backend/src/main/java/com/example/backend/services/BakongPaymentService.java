package com.example.backend.services;

import com.example.backend.dto.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BakongPaymentService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bakong.api.url:https://horrorbringer.site/api/v1}")
    private String apiUrl;

    @Value("${bakong.api.key:bkg_live_xxxxxxxxxxxxxxxxxxxx}")
    private String apiKey;

    public BakongKhqrResponseDTO generateKhqr(BakongKhqrRequestDTO request) {
        String url = apiUrl + "/bakong/khqr/generate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        HttpEntity<BakongKhqrRequestDTO> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(url, entity, BakongKhqrResponseDTO.class);
    }

    public BakongPaymentCheckResponseDTO checkPaymentStatus(BakongPaymentCheckRequestDTO request) {
        String url = apiUrl + "/bakong/payment/check";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        HttpEntity<BakongPaymentCheckRequestDTO> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(url, entity, BakongPaymentCheckResponseDTO.class);
    }
}
