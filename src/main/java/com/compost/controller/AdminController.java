package com.compost.controller;

import com.compost.scheduler.ReminderScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ReminderScheduler reminderScheduler;

    public AdminController(ReminderScheduler reminderScheduler) {
        this.reminderScheduler = reminderScheduler;
    }

    @GetMapping("/test-notify")
    public String showTestNotify(Model model) {
        return "admin/test-notify";
    }

    @PostMapping("/test-notify")
    public String triggerNotify(@RequestParam String shift, RedirectAttributes redirectAttrs) {
        if ("morning".equalsIgnoreCase(shift)) {
            reminderScheduler.sendMorningReminders();
            redirectAttrs.addFlashAttribute("message", "Morning notifications sent for today.");
        } else if ("afternoon".equalsIgnoreCase(shift)) {
            reminderScheduler.sendAfternoonReminders();
            redirectAttrs.addFlashAttribute("message", "Afternoon notifications sent for today.");
        } else {
            redirectAttrs.addFlashAttribute("message", "Unknown shift type.");
        }
        return "redirect:/admin/test-notify";
    }
}
