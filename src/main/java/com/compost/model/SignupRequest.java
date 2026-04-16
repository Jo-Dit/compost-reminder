package com.compost.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SignupRequest {

    @Email
    @Size(max = 254)
    private String email;

    private ShiftSignup.ShiftType shiftType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private List<LocalDate> dates = new ArrayList<>();

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null && !email.isBlank()) ? email.trim() : null;
    }

    public ShiftSignup.ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftSignup.ShiftType shiftType) { this.shiftType = shiftType; }

    public List<LocalDate> getDates() { return dates; }
    public void setDates(List<LocalDate> dates) {
        this.dates = dates != null ? dates : new ArrayList<>();
    }

    public boolean hasEmail() { return email != null && !email.isBlank(); }
    public boolean hasContactInfo() { return hasEmail(); }
}
