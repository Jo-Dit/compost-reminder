package com.compost.repository;

import com.compost.model.ShiftSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ShiftSignupRepository extends JpaRepository<ShiftSignup, Long> {

    /**
     * Find all active signups for a specific shift type and day.
     * Used by the scheduler to find who to notify each day.
     */
    List<ShiftSignup> findByShiftTypeAndDayOfWeekAndActiveTrue(
            ShiftSignup.ShiftType shiftType,
            DayOfWeek dayOfWeek
    );

    /**
     * Find all active signups for a specific shift type.
     * Used to look up all morning or afternoon subscribers.
     */
    List<ShiftSignup> findByShiftTypeAndActiveTrue(ShiftSignup.ShiftType shiftType);

    /** Check for duplicate email+shift+day signup */
    boolean existsByEmailAndShiftTypeAndDayOfWeekAndActiveTrue(
            String email,
            ShiftSignup.ShiftType shiftType,
            DayOfWeek dayOfWeek
    );

    /** Check for duplicate phone+shift+day signup */
    boolean existsByPhoneAndShiftTypeAndDayOfWeekAndActiveTrue(
            String phone,
            ShiftSignup.ShiftType shiftType,
            DayOfWeek dayOfWeek
    );
}
