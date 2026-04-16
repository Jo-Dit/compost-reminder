package com.compost.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_signups")
public class ShiftSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 254)
    private String email;

    @Column(length = 20)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ShiftType shiftType;

    @NotNull
    private LocalDate shiftDate;

    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean active = true;

    public ShiftSignup() {}

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public String getEmail()             { return email; }
    public void setEmail(String email)   { this.email = email; }

    public String getPhone()             { return phone; }
    public void setPhone(String phone)   { this.phone = phone; }

    public ShiftType getShiftType()              { return shiftType; }
    public void setShiftType(ShiftType shiftType){ this.shiftType = shiftType; }

    public LocalDate getShiftDate()              { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate){ this.shiftDate = shiftDate; }

    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    public boolean isActive()            { return active; }
    public void setActive(boolean active){ this.active = active; }

    public enum ShiftType {
        MORNING,
        AFTERNOON
    }


}
