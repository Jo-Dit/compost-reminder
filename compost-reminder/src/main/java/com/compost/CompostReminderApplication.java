package com.compost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CompostReminderApplication {
    public static void main(String[] args) {
        SpringApplication.run(CompostReminderApplication.class, args);
    }
}
