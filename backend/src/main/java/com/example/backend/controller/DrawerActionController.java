package com.example.backend.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.DrawerActionRequestDTO;
import com.example.backend.model.DrawerActionEntity;
import com.example.backend.model.UserEntity;
import com.example.backend.repository.DrawerActionRepository;
import com.example.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drawer-actions")
@RequiredArgsConstructor
public class DrawerActionController {

    private final DrawerActionRepository drawerActionRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> recordAction(@RequestBody DrawerActionRequestDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DrawerActionEntity entity = new DrawerActionEntity();
        entity.setUser(user);
        entity.setType(DrawerActionEntity.ActionType.valueOf(request.getType()));
        entity.setAmount(request.getAmount() != null ? request.getAmount() : 0.0);
        entity.setReason(request.getReason());
        entity.setActionTime(LocalDateTime.now());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        drawerActionRepository.save(entity);
        return ResponseEntity.ok().build();
    }
}
