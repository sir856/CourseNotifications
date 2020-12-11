package com.kpfu.consumer.course_notifications.repository;

import com.kpfu.consumer.course_notifications.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
}
