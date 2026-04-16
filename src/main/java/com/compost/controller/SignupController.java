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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the web signup form (Thymeleaf rendered pages).
 */
@Controller
public class SignupController {

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("EEE, MMM d");

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    /** Show the signup form */
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("signup", new SignupRequest());
        model.addAttribute("shiftTypes", ShiftSignup.ShiftType.values());
        model.addAttribute("dateWeeks", generateWeekdayDateWeeks());
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
            String dates = saved.stream()
                .map(s -> s.getShiftDate().format(DISPLAY_FMT))
                .collect(Collectors.joining(", "));
            redirectAttrs.addFlashAttribute("shift", saved.get(0).getShiftType().name().toLowerCase());
            redirectAttrs.addFlashAttribute("day", dates);
            return "redirect:/success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("signup", request);
            model.addAttribute("shiftTypes", ShiftSignup.ShiftType.values());
            model.addAttribute("dateWeeks", generateWeekdayDateWeeks());
            return "index";
        }
    }

    /** Show the success page after redirect */
    @GetMapping("/success")
    public String showSuccess() {
        return "success";
    }

    /** Returns the next 4 weeks of weekday dates (Mon–Fri), split into groups of 5. */
    private List<List<LocalDate>> generateWeekdayDateWeeks() {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate d = LocalDate.now().plusDays(1);
        while (dates.size() < 20) {
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(d);
            }
            d = d.plusDays(1);
        }
        List<List<LocalDate>> weeks = new ArrayList<>();
        for (int i = 0; i < dates.size(); i += 5) {
            weeks.add(dates.subList(i, i + 5));
        }
        return weeks;
    }
}
