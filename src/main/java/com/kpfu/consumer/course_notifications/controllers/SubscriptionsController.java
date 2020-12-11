package com.kpfu.consumer.course_notifications.controllers;

import com.kpfu.consumer.course_notifications.components.ConsumersComponent;
import com.kpfu.consumer.course_notifications.model.Subscription;
import com.kpfu.consumer.course_notifications.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionsController {

    private final ConsumersComponent consumersComponent;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionsController(ConsumersComponent consumersComponent, SubscriptionRepository subscriptionRepository) {
        this.consumersComponent = consumersComponent;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostMapping("/subscript")
    public Subscription subscript(@RequestBody Subscription subscription) {
        subscription = subscriptionRepository.save(subscription);
        consumersComponent.newSubscription(subscription);
        return subscription;
    }

}
