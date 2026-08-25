package com.event_hub.event_hub.web;

import com.event_hub.event_hub.client.NotificationClient;
import com.event_hub.event_hub.client.UserNotificationPreferenceRequest;
import com.event_hub.event_hub.client.UserNotificationPreferenceResponse;
import com.event_hub.event_hub.exception.BusinessException;
import com.event_hub.event_hub.exception.UnauthorizedAccessException;
import com.event_hub.event_hub.model.entity.user.User;
import com.event_hub.event_hub.service.user.AuthenticationUserDetails;
import com.event_hub.event_hub.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    private final UserService userService;
    private final NotificationClient notificationClient;

    public UserProfileController(UserService userService, NotificationClient notificationClient) {
        this.userService = userService;
        this.notificationClient = notificationClient;
    }

    @GetMapping
    public String showProfile(@AuthenticationPrincipal AuthenticationUserDetails principal, Model model) {
        log.debug("📄 Loading profile for user: {}", principal.getUsername());
        User user = userService.findByUsername(principal.getUsername());
        model.addAttribute("user", user);
        

        try {
            ResponseEntity<UserNotificationPreferenceResponse> preferencesResponse = 
                notificationClient.getNotificationPreferences(user.getId());
            if (preferencesResponse.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("notificationPreferences", preferencesResponse.getBody());
            }
        } catch (Exception e) {
            log.warn("⚠️ Failed to fetch notification preferences: {}", e.getMessage());
            // If microservice is unavailable, show defaults
            UserNotificationPreferenceResponse defaults = new UserNotificationPreferenceResponse();
            defaults.setEmailEnabled(true);
            defaults.setAppAlertsEnabled(true);
            model.addAttribute("notificationPreferences", defaults);
        }
        
        return "profile";
    }

    @PostMapping
    public String updateProfile(@RequestParam String firstName,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam(required = false) String profilePicture,
                                @AuthenticationPrincipal AuthenticationUserDetails principal,
                                RedirectAttributes redirectAttributes) {
        try {
            log.info("✏️ Updating profile for user: {}", principal.getUsername());
            User user = userService.findByUsername(principal.getUsername());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setProfilePicture(profilePicture);
            userService.updateUser(user);
            log.info("✅ Profile updated successfully");
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (BusinessException ex) {
            log.warn("⚠️ Profile update failed: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("❌ Invalid profile data: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/profile";
    }


    @GetMapping("/notification-settings")
    public String showNotificationSettings(@AuthenticationPrincipal AuthenticationUserDetails principal, Model model) {
        log.debug("🔔 Loading notification settings for user: {}", principal.getUsername());
        User user = userService.findByUsername(principal.getUsername());
        
        try {
            ResponseEntity<UserNotificationPreferenceResponse> preferencesResponse = 
                notificationClient.getNotificationPreferences(user.getId());
            if (preferencesResponse.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("preferences", preferencesResponse.getBody());
            }
        } catch (Exception e) {
            log.warn("⚠️ Failed to fetch notification preferences: {}", e.getMessage());
            // If microservice unavailable, show defaults
            UserNotificationPreferenceResponse defaults = new UserNotificationPreferenceResponse();
            defaults.setEmailEnabled(true);
            defaults.setAppAlertsEnabled(true);
            model.addAttribute("preferences", defaults);
        }
        
        model.addAttribute("userId", user.getId());
        return "notification-settings";
    }


    @PutMapping("/notification-settings/{userId}")
    @ResponseBody
    public ResponseEntity<String> saveNotificationSettings(
            @PathVariable java.util.UUID userId,
            @RequestParam boolean emailEnabled,
            @RequestParam boolean smsEnabled,
            @RequestParam boolean appAlertsEnabled,
            @RequestParam(defaultValue = "true") boolean pushNotificationEnabled,
            @AuthenticationPrincipal AuthenticationUserDetails principal) {
        

        User user = userService.findByUsername(principal.getUsername());
        if (!user.getId().equals(userId)) {
            log.warn("🚫 Unauthorized access attempt to update notification settings for user: {}", userId);
            throw new UnauthorizedAccessException("You cannot modify another user's notification settings");
        }

        try {
            log.info("💾 Saving notification settings for user: {}", userId);
            UserNotificationPreferenceRequest request = new UserNotificationPreferenceRequest();
            request.setUserId(userId);
            request.setEmailEnabled(emailEnabled);
            request.setSmsEnabled(smsEnabled);
            request.setAppAlertsEnabled(appAlertsEnabled);
            request.setPushNotificationEnabled(pushNotificationEnabled);

            ResponseEntity<UserNotificationPreferenceResponse> response = 
                notificationClient.updateNotificationPreferences(userId, request);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Notification settings updated successfully");
                return ResponseEntity.ok("Notification preferences updated successfully");
            } else {
                log.error("❌ Failed to update preferences - HTTP {}", response.getStatusCode());
                return ResponseEntity.status(500).body("Failed to update preferences");
            }
        } catch (Exception e) {
            log.error("💥 Error updating notification settings: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    @PostMapping("/notification-settings")
    public String saveNotificationSettingsForm(
            @RequestParam(defaultValue = "false") boolean emailEnabled,
            @RequestParam(defaultValue = "false") boolean smsEnabled,
            @RequestParam(defaultValue = "false") boolean appAlertsEnabled,
            @RequestParam(defaultValue = "false") boolean pushNotificationEnabled,
            @AuthenticationPrincipal AuthenticationUserDetails principal,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("💾 Saving notification settings via form for user: {}", principal.getUsername());
            User user = userService.findByUsername(principal.getUsername());
            
            UserNotificationPreferenceRequest request = new UserNotificationPreferenceRequest();
            request.setUserId(user.getId());
            request.setEmailEnabled(emailEnabled);
            request.setSmsEnabled(smsEnabled);
            request.setAppAlertsEnabled(appAlertsEnabled);
            request.setPushNotificationEnabled(pushNotificationEnabled);

            ResponseEntity<UserNotificationPreferenceResponse> response = 
                notificationClient.updateNotificationPreferences(user.getId(), request);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Notification settings updated successfully");
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Your alert preferences have been updated successfully!");
            } else {
                log.error("❌ Failed to update preferences - HTTP {}", response.getStatusCode());
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update preferences");
            }
        } catch (Exception e) {
            log.error("💥 Error updating notification settings: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating preferences: " + e.getMessage());
        }

        return "redirect:/profile/notification-settings";
    }
}
