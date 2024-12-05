package com.alhashim.oneIT.repositories;

import com.alhashim.oneIT.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
