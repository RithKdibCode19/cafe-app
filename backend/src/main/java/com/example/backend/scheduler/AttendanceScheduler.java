package com.example.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.backend.services.AttendanceService;

@Component
public class AttendanceScheduler {

    private final AttendanceService attendanceService;

    public AttendanceScheduler(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Runs daily at 23:59 to auto-close any open attendance records
     * where the employee forgot to clock out.
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void autoCloseOpenShifts() {
        int closed = attendanceService.autoCloseOpenAttendance();
        if (closed > 0) {
            System.out.println("[AttendanceScheduler] Auto-closed " + closed + " open attendance record(s).");
        }
    }
}
