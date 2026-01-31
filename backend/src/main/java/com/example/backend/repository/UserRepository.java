package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Find all active users (not deleted)
    List<UserEntity> findAllByDeletedAtIsNull();

    // Find active user by ID
    Optional<UserEntity> findByUserIdAndDeletedAtIsNull(Long userId);

    // Find by username (for authentication)
    Optional<UserEntity> findByUserNameAndDeletedAtIsNull(String userName);

    // Check if username exists
    boolean existsByUserNameAndDeletedAtIsNull(String userName);
}
