package com.kpfu.consumer.course_notifications.repository;

import com.kpfu.consumer.course_notifications.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс репозитория для подключения к базе данных
 *
 * @author Ильфат Саяхов
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
}
