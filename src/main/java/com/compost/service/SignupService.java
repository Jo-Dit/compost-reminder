package com.compost.service;

import com.compost.model.ShiftSignup;
import com.compost.model.SignupRequest;
import com.compost.repository.ShiftSignupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        if (!request.hasContactInfo()) {
            throw new IllegalArgumentException(
                "Please provide an email address.");
        }

        if (request.getShiftType() == null) {
            throw new IllegalArgumentException("Please select a shift (Morning or Afternoon).");
        }
        if (request.getDates() == null || request.getDates().isEmpty()) {
            throw new IllegalArgumentException("Please select at least one date.");
        }

        List<ShiftSignup> created = new ArrayList<>();
        for (LocalDate date : request.getDates()) {
            boolean emailDup = request.hasEmail() &&
                repository.existsByEmailAndShiftTypeAndShiftDateAndActiveTrue(
                    request.getEmail(), request.getShiftType(), date);

            if (emailDup) {
                continue; // skip dates already registered
            }

            ShiftSignup signup = new ShiftSignup();
            signup.setEmail(request.getEmail());
            signup.setShiftType(request.getShiftType());
            signup.setShiftDate(date);

            ShiftSignup saved = repository.save(signup);
            log.info("Signup saved: id={}, shift={}, date={}, email={}",
                saved.getId(), saved.getShiftType(), saved.getShiftDate(),
                saved.getEmail() != null);
            created.add(saved);
        }

        if (created.isEmpty()) {
            throw new IllegalArgumentException(
                "You're already signed up for all selected dates on that shift!");
        }

        return created;
    }
}
