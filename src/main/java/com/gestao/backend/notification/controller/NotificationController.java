package com.gestao.backend.notification.controller;

import com.gestao.backend.core.security.SecurityUtils;
import com.gestao.backend.notification.entity.Notification;
import com.gestao.backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        java.util.UUID companyId = SecurityUtils.getCurrentCompanyId();
        return ResponseEntity.ok(notificationService.getNotifications(companyId, page, size));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        java.util.UUID companyId = SecurityUtils.getCurrentCompanyId();
        notificationService.markAsRead(id, companyId);
        return ResponseEntity.ok().build();
    }
}