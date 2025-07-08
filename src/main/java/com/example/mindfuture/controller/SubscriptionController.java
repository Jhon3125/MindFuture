package com.example.mindfuture.controller;

import com.example.mindfuture.services.SubscriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/upgrade")
    public String showUpgradeForm(Model model) {
        if (subscriptionService.isPremium()) {
            return "redirect:/?alreadyPremium=true";
        }
        model.addAttribute("precio", "49.99");
        return "subscription/upgrade";
    }

    @PostMapping("/process-payment")
    public String processPayment() {
        subscriptionService.upgradeToPremium();
        return "redirect:/subscription/confirmation";
    }

    @GetMapping("/confirmation")
    public String showConfirmation() {
        if (!subscriptionService.isPremium()) {
            return "redirect:/subscription/upgrade";
        }
        return "subscription/confirmation";
    }
}