package com.compost.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.compost.model.ShiftSignup;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

/**
 * Sends reminders via:
 *   1. Email — Resend API
 *   2. SMS   — free email-to-SMS gateway via Resend
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Value("${notification.from-email}")
    private String fromEmail;

    @Value("${notification.club-name}")
    private String clubName;

    @Value("${notification.morning.message}")
    private String morningMessage;

    @Value("${notification.afternoon.message}")
    private String afternoonMessage;

    public void sendReminder(ShiftSignup signup) {
        String message = buildMessage(signup.getShiftType());
        String subject = buildSubject(signup.getShiftType());

        if (signup.getEmail() != null) {
            sendEmail(signup.getEmail(), subject, message + "\n\n— " + clubName);
            log.info("Email reminder sent to {}", signup.getEmail());
        }

        if (signup.getPhone() != null && signup.getCarrier() != null) {
            String smsAddress = signup.getCarrier().toSmsAddress(signup.getPhone());
            sendEmail(smsAddress, subject, truncateForSms(message));
            log.info("SMS reminder sent to {} via {} gateway",
                signup.getPhone(), signup.getCarrier().getDisplayName());
        }
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            String apiKey = System.getenv("RESEND_API_KEY");
            log.info("RESEND_API_KEY present: {}", apiKey != null && !apiKey.isBlank());
            log.info("Sending email from: {}", fromEmail);
            log.info("Sending email to: {}, subject: {}", to, subject);

            Resend resend = new Resend(apiKey);
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .text(body)
                    .build();

            log.info("About to call Resend API for: {}", to);
            var response = resend.emails().send(params);
            log.info("Resend full response: {}", response);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }

    private String buildMessage(ShiftSignup.ShiftType shiftType) {
        return shiftType == ShiftSignup.ShiftType.MORNING ? morningMessage : afternoonMessage;
    }

    private String buildSubject(ShiftSignup.ShiftType shiftType) {
        return String.format("[%s] %s Shift Reminder", clubName,
            shiftType == ShiftSignup.ShiftType.MORNING ? "Morning" : "Afternoon");
    }

    private String truncateForSms(String message) {
        if (message.length() <= 160) return message;
        return message.substring(0, 157) + "...";
    }
}
