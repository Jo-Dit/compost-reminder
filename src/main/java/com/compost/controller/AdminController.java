package com.compost.controller;

import com.compost.scheduler.ReminderScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReminderScheduler reminderScheduler;

    public AdminController(ReminderScheduler reminderScheduler) {
        this.reminderScheduler = reminderScheduler;
    }

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("EEE, MMM d");

    @GetMapping("/test-notify")
    public String showTestNotify(Model model) {
        model.addAttribute("today", LocalDate.now().format(DISPLAY_FMT));
        return "admin/test-notify";
    }

    @PostMapping("/test-notify")
    public String triggerNotify(@RequestParam String shift, RedirectAttributes redirectAttrs) {
        String dateLabel = LocalDate.now().format(DISPLAY_FMT);
        if ("morning".equalsIgnoreCase(shift)) {
            reminderScheduler.sendMorningReminders();
            redirectAttrs.addFlashAttribute("message", "Morning notifications sent for " + dateLabel + ".");
        } else if ("afternoon".equalsIgnoreCase(shift)) {
            reminderScheduler.sendAfternoonReminders();
            redirectAttrs.addFlashAttribute("message", "Afternoon notifications sent for " + dateLabel + ".");
        } else {
            redirectAttrs.addFlashAttribute("message", "Unknown shift type.");
        }
        return "redirect:/admin/test-notify";
    }
}
