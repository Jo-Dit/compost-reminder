package com.compost.service;

import com.compost.model.ShiftSignup;
import com.compost.model.SignupRequest;
import com.compost.repository.ShiftSignupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Service
public class SignupService {

    private static final Logger log = LoggerFactory.getLogger(SignupService.class);
    private final ShiftSignupRepository repository;

    public SignupService(ShiftSignupRepository repository) {
        this.repository = repository;
    }

    public List<ShiftSignup> createSignups(SignupRequest request) {
        // Must have at least one valid contact method
        if (!request.hasContactInfo()) {
            throw new IllegalArgumentException(
                "Please provide an email address or a phone number.");
        }

        if (request.getShiftType() == null) {
            throw new IllegalArgumentException("Please select a shift (Morning or Afternoon).");
        }
        if (request.getDaysOfWeek() == null || request.getDaysOfWeek().isEmpty()) {
            throw new IllegalArgumentException("Please select at least one day.");
        }

        List<ShiftSignup> created = new ArrayList<>();
        for (DayOfWeek day : request.getDaysOfWeek()) {
            boolean emailDup = request.hasEmail() &&
                repository.existsByEmailAndShiftTypeAndDayOfWeekAndActiveTrue(
                    request.getEmail(), request.getShiftType(), day);
            boolean smsDup = request.hasPhone() &&
                repository.existsByPhoneAndShiftTypeAndDayOfWeekAndActiveTrue(
                    request.getPhone(), request.getShiftType(), day);

            if (emailDup || smsDup) {
                continue; // skip days already registered
            }

            ShiftSignup signup = new ShiftSignup();
            signup.setEmail(request.getEmail());
            signup.setPhone(request.getPhone());
            signup.setShiftType(request.getShiftType());
            signup.setDayOfWeek(day);

            ShiftSignup saved = repository.save(signup);
            log.info("Signup saved: id={}, shift={}, day={}, email={}, sms={}",
                saved.getId(), saved.getShiftType(), saved.getDayOfWeek(),
                saved.getEmail() != null, saved.getPhone() != null);
            created.add(saved);
        }

        if (created.isEmpty()) {
            throw new IllegalArgumentException(
                "You're already signed up for all selected days on that shift!");
        }

        return created;
    }
}
