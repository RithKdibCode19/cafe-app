package com.example.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.model.CustomerEntity;
import com.example.backend.model.OrderEntity;
import com.example.backend.repository.CustomerRepository;

@Service
public class LoyaltyService {

    private final CustomerRepository customerRepository;
    private final SystemSettingService systemSettingService;

    public LoyaltyService(CustomerRepository customerRepository, SystemSettingService systemSettingService) {
        this.customerRepository = customerRepository;
        this.systemSettingService = systemSettingService;
    }

    public double getRedeemRate() {
        String rateStr = systemSettingService.getValue("LOYALTY_REDEEM_RATE");
        if (rateStr != null) {
            try {
                return Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                // fall back to default
            }
        }
        return 0.01; // Default: 100 points = $1
    }

    public double getEarnRate() {
        String rateStr = systemSettingService.getValue("LOYALTY_EARN_RATE");
        if (rateStr != null) {
            try {
                return Double.parseDouble(rateStr);
            } catch (NumberFormatException e) {
                // fall back to default
            }
        }
        return 1.0; // Default: $1 = 1 point
    }

    @Transactional
    public void awardPoints(OrderEntity order) {
        if (order.getCustomer() == null || order.getStatus() != OrderEntity.OrderStatus.PAID) {
            return;
        }

        CustomerEntity customer = order.getCustomer();
        double earnRate = getEarnRate();
        
        int pointsToGain = (int) Math.floor(order.getTotalAmount() * earnRate);
        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(currentPoints + pointsToGain);

        updateMembershipLevel(customer);
        customerRepository.save(customer);
    }

    @Transactional
    public void revertPoints(OrderEntity order) {
        if (order.getCustomer() == null) return;

        CustomerEntity customer = order.getCustomer();
        double earnRate = getEarnRate();
        
        int pointsToRemove = (int) Math.floor(order.getTotalAmount() * earnRate);
        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;

        customer.setLoyaltyPoints(Math.max(0, currentPoints - pointsToRemove));
        customerRepository.save(customer);
    }

    @Transactional
    public void deductPoints(CustomerEntity customer, int points) {
        if (customer == null || points <= 0) return;
        
        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(Math.max(0, currentPoints - points));
        customerRepository.save(customer);
    }

    @Transactional
    public void refundPoints(CustomerEntity customer, int points) {
        if (customer == null || points <= 0) return;
        
        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(currentPoints + points);
        customerRepository.save(customer);
    }

    private void updateMembershipLevel(CustomerEntity customer) {
        int totalPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        
        int silverThreshold = 300;
        int goldThreshold = 1000;
        
        try {
            String silverVal = systemSettingService.getValue("LOYALTY_SILVER_THRESHOLD");
            if (silverVal != null) silverThreshold = Integer.parseInt(silverVal);
            
            String goldVal = systemSettingService.getValue("LOYALTY_GOLD_THRESHOLD");
            if (goldVal != null) goldThreshold = Integer.parseInt(goldVal);
        } catch (Exception e) {}

        if (totalPoints >= goldThreshold) {
            customer.setMembershipLevel("GOLD");
        } else if (totalPoints >= silverThreshold) {
            customer.setMembershipLevel("SILVER");
        } else {
            customer.setMembershipLevel("BRONZE");
        }
    }
}
