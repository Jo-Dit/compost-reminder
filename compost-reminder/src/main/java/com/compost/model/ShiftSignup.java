package com.compost.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_signups")
@Getter
@Setter
@NoArgsConstructor
public class ShiftSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 254)
    private String email;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Carrier carrier;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean active = true;

    public enum ShiftType {
        MORNING,
        AFTERNOON
    }

    /**
     * US mobile carriers with their email-to-SMS gateway domains.
     * SMS is sent by emailing {digits}@{gateway} — no Twilio needed.
     */
    public enum Carrier {
        VERIZON("Verizon",              "vtext.com"),
        ATT("AT&T",                     "txt.att.net"),
        TMOBILE("T-Mobile",             "tmomail.net"),
        BOOST("Boost Mobile",           "sms.myboostmobile.com"),
        CRICKET("Cricket",              "sms.cricketwireless.net"),
        METRO("Metro by T-Mobile",      "mymetropcs.com"),
        SPRINT("Sprint",                "messaging.sprintpcs.com"),
        US_CELLULAR("US Cellular",      "email.uscc.net");

        private final String displayName;
        private final String gateway;

        Carrier(String displayName, String gateway) {
            this.displayName = displayName;
            this.gateway = gateway;
        }

        public String getDisplayName() { return displayName; }
        public String getGateway()     { return gateway; }

        /** Builds the full gateway email, e.g. 5551234567@vtext.com */
        public String toSmsAddress(String phoneNumber) {
            String digits = phoneNumber.replaceAll("[^0-9]", "");
            return digits + "@" + gateway;
        }
    }
}
