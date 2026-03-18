package com.compost.controller;

import com.compost.model.ShiftSignup;
import com.compost.model.SignupRequest;
import com.compost.repository.ShiftSignupRepository;
import com.compost.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API endpoints for the compost reminder app.
 *
 * POST /api/signup        → Register a new shift signup
 * GET  /api/signups       → List all active signups (admin use)
 * DELETE /api/signups/{id} → Deactivate a signup (cancel reminders)
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final SignupService signupService;
    private final ShiftSignupRepository repository;

    public ApiController(SignupService signupService, ShiftSignupRepository repository) {
        this.signupService = signupService;
        this.repository = repository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createSignup(@Valid @RequestBody SignupRequest request) {
        try {
            List<ShiftSignup> saved = signupService.createSignups(request);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", saved.size(),
                    "message", "Signed up for " + saved.size() + " shift(s)."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/signups")
    public ResponseEntity<List<ShiftSignup>> listSignups() {
        return ResponseEntity.ok(repository.findAll());
    }

    @DeleteMapping("/signups/{id}")
    public ResponseEntity<?> cancelSignup(@PathVariable Long id) {
        return repository.findById(id).map(signup -> {
            signup.setActive(false);
            repository.save(signup);
            return ResponseEntity.ok(Map.of("success", true, "message", "Reminder cancelled."));
        }).orElse(ResponseEntity.notFound().build());
    }
}
