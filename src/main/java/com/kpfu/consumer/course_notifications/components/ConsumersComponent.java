package com.kpfu.consumer.course_notifications.components;

import com.kpfu.consumer.course_notifications.model.Subscription;
import com.kpfu.consumer.course_notifications.repository.SubscriptionRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsumersComponent {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private List<KafkaMessageListenerContainer<Integer, String>> containers = new ArrayList<>();
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public ConsumersComponent(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    public void init() {
        log.info("Start init containers");

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {
            log.debug(subscription.getId() + " : " + subscription.getTags());
            containers.add(startContainer(subscription.getUserId(), message -> log.info("received for user " + subscription.getUserId() + " : "  + message.value()), subscription.getTags().toArray(new String[0])));
        }

    }

    private KafkaMessageListenerContainer<Integer, String> startContainer(int userId, MessageListener<Integer, String> listener, String... topics) {
        ContainerProperties containerProps = new ContainerProperties(topics);
        containerProps.setMessageListener(listener);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "main_group" + userId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<>(props);
        KafkaMessageListenerContainer<Integer, String> container =
                new KafkaMessageListenerContainer<>(cf, containerProps);

        container.setBeanName("testAuto" + userId);
        container.start();
        return container;
    }
}
