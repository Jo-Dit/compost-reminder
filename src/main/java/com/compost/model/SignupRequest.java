package com.compost.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class SignupRequest {

    @Email
    @Size(max = 254)
    private String email;

    private ShiftSignup.ShiftType shiftType;
    private List<DayOfWeek> daysOfWeek = new ArrayList<>();

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null && !email.isBlank()) ? email.trim() : null;
    }

    public ShiftSignup.ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftSignup.ShiftType shiftType) { this.shiftType = shiftType; }

    public List<DayOfWeek> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek != null ? daysOfWeek : new ArrayList<>();
    }

    public boolean hasEmail() { return email != null && !email.isBlank(); }
    public boolean hasContactInfo() { return hasEmail(); }
}
