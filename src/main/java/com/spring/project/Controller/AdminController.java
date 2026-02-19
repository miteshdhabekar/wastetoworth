package com.spring.project.Controller;

import com.spring.project.entity.Customer;
import com.spring.project.entity.Donation;
import com.spring.project.repository.Customerrepo;
import com.spring.project.repository.DonationRepository;
import com.spring.project.service.MailService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "${frontend.url}")
public class AdminController {



    @Autowired
    private Customerrepo customerrepo;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private MailService mailService;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Donation> allDonations = donationRepository.findAll();

        double totalWaste = allDonations.stream().mapToDouble(Donation::getQuantity).sum();
        long totalUsers = customerrepo.count();

        double wasteCollectedToday = allDonations.stream()
                .filter(d -> d.getDonationDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Donation::getQuantity)
                .sum();

        stats.put("totalWaste", totalWaste);
        stats.put("totalUsers", totalUsers);
        stats.put("wasteCollectedToday", wasteCollectedToday);

        return stats;
    }

    @GetMapping("/users")
    public List<Customer> getAllUsers() {
        return customerrepo.findAll();
    }

    @GetMapping("/users/pending")
    public List<Customer> getPendingNGOUsers() {
        return customerrepo.findAll().stream()
                .filter(c -> "NGO".equalsIgnoreCase(c.getRole()) && "PENDING".equalsIgnoreCase(c.getStatus()))
                .collect(Collectors.toList());
    }

    @PostMapping("/approve")
    public String approveUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<Customer> userOpt = customerrepo.findByEmail(email);
        if (userOpt.isPresent()) {
            Customer user = userOpt.get();
            user.setStatus("APPROVED");
            customerrepo.save(user);
            mailService.sendApprovalEmail(user.getEmail(), user.getName());
            return "NGO user approved successfully.";
        }
        return "User not found.";
    }

    @PostMapping("/reject")
    public String rejectUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<Customer> userOpt = customerrepo.findByEmail(email);
        if (userOpt.isPresent()) {
            Customer user = userOpt.get();
            customerrepo.delete(user);
            mailService.sendRejectionEmail(email, user.getName());
            return "NGO user rejected and deleted successfully.";
        }
        return "User not found.";
    }

    @DeleteMapping("/users/{email}")
    public String deleteUser(@PathVariable String email) {
        Optional<Customer> userOpt = customerrepo.findByEmail(email);
        if (userOpt.isPresent()) {
            customerrepo.delete(userOpt.get());
            return "User deleted successfully.";
        }
        return "User not found.";
    }

    @GetMapping("/report/pie")
    public List<Map<String, Object>> getPieChartData() {
        List<Donation> donations = donationRepository.findAll();
        Map<String, Long> counts = donations.stream()
                .collect(Collectors.groupingBy(
                        d -> "ROTTEN".equalsIgnoreCase(d.getFoodType()) ? "Rotten" : "Fresh",
                        Collectors.counting()));

        return counts.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("foodType", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/report/bar")
    public List<Map<String, Object>> getBarChartData() {
        List<Donation> donations = donationRepository.findAll();
        Map<String, Long> counts = donations.stream()
                .collect(Collectors.groupingBy(Donation::getStatus, Collectors.counting()));

        return counts.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
