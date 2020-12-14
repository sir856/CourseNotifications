package com.kpfu.consumer.course_notifications.controllers;

import com.kpfu.consumer.course_notifications.components.ConsumersComponent;
import com.kpfu.consumer.course_notifications.model.Subscription;
import com.kpfu.consumer.course_notifications.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Котроллер для управления подписками
 *
 * @author Ильфат Саяхов
 */
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController {

    private final ConsumersComponent consumersComponent;
    private final SubscriptionRepository subscriptionRepository;


    /**
     * Конструктор для инъекции {@link ConsumersComponent} и {@link SubscriptionRepository}
     *
     * @param consumersComponent
     * @param subscriptionRepository
     */
    @Autowired
    public SubscriptionsController(ConsumersComponent consumersComponent, SubscriptionRepository subscriptionRepository) {
        this.consumersComponent = consumersComponent;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Метод для обработки POST-запроса. При выполнении запроса в базу данных и Apache Kafka добавляется(обновляется) информация о подписках польхователя.
     * Информация о пользователе отправляется в виде объекта JSON, пример:
     * {
     *     "userId": 1,
     *     "tags": ["topic1", "topic2"]
     * }
     *
     * @param subscription - информация о пользователе в виде объекта JSON
     * @return информация о подписках пользователя
     * @see Subscription
     * @see ConsumersComponent#newSubscription(Subscription)
     */
    @PostMapping(value = "/subscript")
    public Subscription subscript(@RequestBody Subscription subscription) {
        subscription = subscriptionRepository.save(subscription);
        consumersComponent.newSubscription(subscription);
        return subscription;
    }

}
