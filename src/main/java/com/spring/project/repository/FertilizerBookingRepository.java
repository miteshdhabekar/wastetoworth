package com.spring.project.repository;

import com.spring.project.entity.FertilizerBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FertilizerBookingRepository extends JpaRepository<FertilizerBooking, Long> {
}
