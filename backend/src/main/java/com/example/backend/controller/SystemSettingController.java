package com.example.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.SystemSettingEntity;
import com.example.backend.services.SystemSettingService;


@RestController
@RequestMapping("/api/settings")
public class SystemSettingController {

    private final SystemSettingService service;

    public SystemSettingController(SystemSettingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SystemSettingEntity>> getAllSettings() {
        return ResponseEntity.ok(service.getAllSettings());
    }

    @PostMapping("/batch")
    public ResponseEntity<Void> updateSettings(@RequestBody Map<String, String> settings) {
        service.updateSettings(settings);
        return ResponseEntity.ok().build();
    }
}
