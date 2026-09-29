package com.gestao.backend.notification.service;

import com.gestao.backend.notification.entity.Notification;
import com.gestao.backend.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> getNotifications(java.util.UUID companyId, int page, int size) {
        return notificationRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, PageRequest.of(page, size));
    }

    @Transactional
    public void markAsRead(Long id, java.util.UUID companyId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        if (!notification.getCompanyId().equals(companyId)) {
            throw new RuntimeException("Acesso negado");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}