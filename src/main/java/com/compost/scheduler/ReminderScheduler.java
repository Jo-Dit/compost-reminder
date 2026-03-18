package com.compost.scheduler;

import com.compost.model.ShiftSignup;
import com.compost.repository.ShiftSignupRepository;
import com.compost.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Fires scheduled reminders for morning and afternoon shifts.
 *
 * Cron expressions are configured in application.properties:
 *   notification.morning.cron   → default 7:00 AM daily
 *   notification.afternoon.cron → default 12:00 PM daily
 *
 * Each run checks today's day-of-week and notifies only the
 * people signed up for that specific day.
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final ShiftSignupRepository repository;
    private final NotificationService notificationService;

    public ReminderScheduler(ShiftSignupRepository repository,
                             NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }

    /**
     * Morning reminder – fires at 7:00 AM every day.
     * Finds everyone with a MORNING shift on today's day of week.
     */
    @Scheduled(cron = "${notification.morning.cron}")
    public void sendMorningReminders() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        log.info("Running morning reminders for {}", today);

        List<ShiftSignup> signups = repository
                .findByShiftTypeAndDayOfWeekAndActiveTrue(ShiftSignup.ShiftType.MORNING, today);

        log.info("Found {} morning signup(s) to notify", signups.size());
        signups.forEach(notificationService::sendReminder);
    }

    /**
     * Afternoon reminder – fires at 12:00 PM every day.
     * Finds everyone with an AFTERNOON shift on today's day of week.
     */
    @Scheduled(cron = "${notification.afternoon.cron}")
    public void sendAfternoonReminders() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        log.info("Running afternoon reminders for {}", today);

        List<ShiftSignup> signups = repository
                .findByShiftTypeAndDayOfWeekAndActiveTrue(ShiftSignup.ShiftType.AFTERNOON, today);

        log.info("Found {} afternoon signup(s) to notify", signups.size());
        signups.forEach(notificationService::sendReminder);
    }
}
