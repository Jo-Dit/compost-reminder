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

    @Size(max = 20)
    private String phone;
    private ShiftSignup.Carrier carrier;
    private ShiftSignup.ShiftType shiftType;
    private List<DayOfWeek> daysOfWeek = new ArrayList<>();

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null && !email.isBlank()) ? email.trim() : null;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = (phone != null && !phone.isBlank()) ? phone.trim() : null;
    }

    public ShiftSignup.Carrier getCarrier() { return carrier; }
    public void setCarrier(ShiftSignup.Carrier carrier) { this.carrier = carrier; }

    public ShiftSignup.ShiftType getShiftType() { return shiftType; }
    public void setShiftType(ShiftSignup.ShiftType shiftType) { this.shiftType = shiftType; }

    public List<DayOfWeek> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(List<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek != null ? daysOfWeek : new ArrayList<>();
    }

    public boolean hasEmail() { return email != null && !email.isBlank(); }

    /** Phone is valid for SMS only when both number AND carrier are provided */
    public boolean hasSms() { return phone != null && !phone.isBlank() && carrier != null; }

    public boolean hasContactInfo() { return hasEmail() || hasSms(); }
}
