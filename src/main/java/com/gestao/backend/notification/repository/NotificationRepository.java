package com.gestao.backend.notification.repository;

import com.gestao.backend.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByCompanyIdOrderByCreatedAtDesc(Long companyId, Pageable pageable);
    List<Notification> findByCompanyIdAndReadFalse(Long companyId);
}