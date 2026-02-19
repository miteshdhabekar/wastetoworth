package com.spring.project.repository;

import com.spring.project.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    // 🟡 Get all donations by a specific donor
    List<Donation> findByCustomerEmail(String customerEmail);

    // 🟢 Total quantity of food donated
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d")
    double getTotalQuantity();

    // 🟢 Total quantity collected today
    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM Donation d WHERE DATE(d.donationDate) = CURRENT_DATE")
    double getTodayCollectedQuantity();

    // 🟣 Get donations by status (e.g., Pending, Accepted)
    List<Donation> findByStatus(String status);

    // 🟠 Pie chart data: count grouped by is_rotten (true/false/null)
    @Query("SELECT d.isRotten, COUNT(d) FROM Donation d GROUP BY d.isRotten")
    List<Object[]> getDonationCountByType();

    // 🔵 Bar chart data: count grouped by status (Pending, Accepted, Rejected)
    @Query("SELECT d.status, COUNT(d) FROM Donation d GROUP BY d.status")
    List<Object[]> getDonationCountByStatus();
}
