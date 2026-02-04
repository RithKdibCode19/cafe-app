package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.model.AttendanceEntity;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {
    List<AttendanceEntity> findAllByDeletedAtIsNull();

    List<AttendanceEntity> findByEmployeeEmployeeIdAndDeletedAtIsNull(Long employeeId);

    List<AttendanceEntity> findByEmployeeEmployeeIdAndCheckInBetweenAndDeletedAtIsNull(
            Long employeeId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end);

    java.util.Optional<AttendanceEntity> findTopByEmployeeEmployeeIdAndCheckOutIsNullAndDeletedAtIsNullOrderByCheckInDesc(
            Long employeeId);
}
