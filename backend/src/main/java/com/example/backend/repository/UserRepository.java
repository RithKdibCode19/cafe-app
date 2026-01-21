package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
}
