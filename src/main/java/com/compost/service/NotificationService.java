package com.compost.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.compost.model.ShiftSignup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sends reminders via:
 *   1. Email — SendGrid API
 *   2. SMS   — Twilio
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
            sendEmail(signup.getEmail(), subject, message);
            log.info("Email reminder sent to {}", signup.getEmail());
        }

        if (signup.getPhone() != null) {
            sendSms(signup.getPhone(), truncateForSms(message));
        }
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            String apiKey = System.getenv("SENDGRID_API_KEY");
            log.info("SENDGRID_API_KEY present: {}", apiKey != null && !apiKey.isBlank());
            log.info("Sending email from: {}", fromEmail);
            log.info("Sending email to: {}, subject: {}", to, subject);

            Mail mail = new Mail(new Email(fromEmail), subject, new Email(to), new Content("text/plain", body));

            SendGrid sg = new SendGrid(apiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            log.info("About to call SendGrid API for: {}", to);
            Response response = sg.api(request);
            log.info("SendGrid full response: status={}, body={}, headers={}", response.getStatusCode(), response.getBody(), response.getHeaders());
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }

    private void sendSms(String phone, String body) {
        try {
            String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
            String authToken  = System.getenv("TWILIO_AUTH_TOKEN");
            String fromNumber = System.getenv("TWILIO_FROM_NUMBER");

            log.info("TWILIO_ACCOUNT_SID present: {}", accountSid != null && !accountSid.isBlank());
            log.info("TWILIO_AUTH_TOKEN present: {}", authToken != null && !authToken.isBlank());
            log.info("TWILIO_FROM_NUMBER: {}", fromNumber);

            String digits = phone.replaceAll("[^0-9]", "");
            String toNumber = "+" + (digits.startsWith("1") ? digits : "1" + digits);
            log.info("About to call Twilio API for: {}", toNumber);

            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(fromNumber),
                    body
            ).create();

            log.info("Twilio SMS sent: sid={}, status={}", message.getSid(), message.getStatus());
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", phone, e);
        }
    }

    private String buildMessage(ShiftSignup.ShiftType shiftType) {
        return shiftType == ShiftSignup.ShiftType.MORNING ? morningMessage : afternoonMessage;
    }

    private String buildSubject(ShiftSignup.ShiftType shiftType) {
        return shiftType == ShiftSignup.ShiftType.MORNING ? "Morning Shift Reminder" : "Afternoon Shift Reminder";
    }

    private String truncateForSms(String message) {
        if (message.length() <= 160) return message;
        return message.substring(0, 157) + "...";
    }
}
