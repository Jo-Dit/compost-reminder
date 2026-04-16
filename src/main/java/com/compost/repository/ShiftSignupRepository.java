package com.compost.repository;

import com.compost.model.ShiftSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftSignupRepository extends JpaRepository<ShiftSignup, Long> {

    /**
     * Find all active signups for a specific shift type and exact date.
     * Used by the scheduler to find who to notify each day.
     */
    List<ShiftSignup> findByShiftTypeAndShiftDateAndActiveTrue(
            ShiftSignup.ShiftType shiftType,
            LocalDate shiftDate
    );

    /**
     * Find all active signups for a specific shift type.
     */
    List<ShiftSignup> findByShiftTypeAndActiveTrue(ShiftSignup.ShiftType shiftType);

    /** Check for duplicate email + shift + date signup */
    boolean existsByEmailAndShiftTypeAndShiftDateAndActiveTrue(
            String email,
            ShiftSignup.ShiftType shiftType,
            LocalDate shiftDate
    );
}
