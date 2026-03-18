package com.compost.service;

import com.compost.model.ShiftSignup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends reminders via:
 *   1. Email — standard SMTP (Gmail etc.)
 *   2. SMS   — free email-to-SMS gateway (no Twilio needed)
 *
 * For SMS, we construct a gateway address like 5551234567@vtext.com
 * and send it as a plain email. The carrier converts it to a text.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${notification.club-name}")
    private String clubName;

    @Value("${notification.morning.message}")
    private String morningMessage;

    @Value("${notification.afternoon.message}")
    private String afternoonMessage;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReminder(ShiftSignup signup) {
        String message = buildMessage(signup.getShiftType());
        String subject = buildSubject(signup.getShiftType());

        // Send email reminder
        if (signup.getEmail() != null) {
            sendEmail(signup.getEmail(), subject, message, false);
            log.info("Email reminder sent to {}", signup.getEmail());
        }

        // Send SMS via carrier gateway (free — no Twilio)
        if (signup.getPhone() != null && signup.getCarrier() != null) {
            String smsAddress = signup.getCarrier().toSmsAddress(signup.getPhone());
            // SMS via gateway: keep subject empty and body short (160 chars max for SMS)
            sendEmail(smsAddress, "", truncateForSms(message), true);
            log.info("SMS reminder sent to {} via {} gateway ({})",
                signup.getPhone(), signup.getCarrier().getDisplayName(), smsAddress);
        }
    }

    // ── Private helpers ────────────────────────────────────────

    private String buildMessage(ShiftSignup.ShiftType shiftType) {
        return shiftType == ShiftSignup.ShiftType.MORNING ? morningMessage : afternoonMessage;
    }

    private String buildSubject(ShiftSignup.ShiftType shiftType) {
        return String.format("[%s] %s Shift Reminder 🌱", clubName,
            shiftType == ShiftSignup.ShiftType.MORNING ? "Morning" : "Afternoon");
    }

    /**
     * SMS messages should be under 160 characters.
     * Gateway emails typically ignore the subject line.
     */
    private String truncateForSms(String message) {
        if (message.length() <= 160) return message;
        return message.substring(0, 157) + "...";
    }

    private void sendEmail(String to, String subject, String body, boolean isSmsGateway) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromEmail);
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(isSmsGateway ? body : body + "\n\n— " + clubName);
            mailSender.send(mail);
        } catch (Exception e) {
            log.error("Failed to send {} to {}: {}",
                isSmsGateway ? "SMS gateway email" : "email", to, e.getMessage());
        }
    }
}
