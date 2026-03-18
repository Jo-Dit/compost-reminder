package com.compost.controller;

import com.compost.model.ShiftSignup;
import com.compost.model.SignupRequest;
import com.compost.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the web signup form (Thymeleaf rendered pages).
 */
@Controller
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    /** Show the signup form */
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("signup", new SignupRequest());
        model.addAttribute("shiftTypes", ShiftSignup.ShiftType.values());
        return "index";
    }

    /** Process the form submission */
    @PostMapping("/signup")
    public String processSignup(
            @Valid @ModelAttribute("signup") SignupRequest request,
            RedirectAttributes redirectAttrs,
            Model model) {
        try {
            List<ShiftSignup> saved = signupService.createSignups(request);
            String days = saved.stream()
                .map(s -> capitalizeDay(s.getDayOfWeek()))
                .collect(Collectors.joining(", "));
            redirectAttrs.addFlashAttribute("shift", saved.get(0).getShiftType().name().toLowerCase());
            redirectAttrs.addFlashAttribute("day", days);
            return "redirect:/success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("signup", request);
            model.addAttribute("shiftTypes", ShiftSignup.ShiftType.values());
            return "index";
        }
    }

    /** Show the success page after redirect */
    @GetMapping("/success")
    public String showSuccess() {
        return "success";
    }


    private String capitalizeDay(DayOfWeek day) {
        String name = day.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
