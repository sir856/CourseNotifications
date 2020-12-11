package com.kpfu.consumer.course_notifications.components;

import com.kpfu.consumer.course_notifications.model.Subscription;
import com.kpfu.consumer.course_notifications.repository.SubscriptionRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class ConsumersComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<Integer, KafkaMessageListenerContainer<Integer, String>> containers = new HashMap<>();
    private final SubscriptionRepository subscriptionRepository;
    private boolean inited = false;

    @Autowired
    public ConsumersComponent(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("Start init containers");

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {
            containers.put(subscription.getUserId(), startContainer(subscription.getUserId(),
                    message -> logger.info("received for user " + subscription.getUserId() + " : "  + message.value()),
                    subscription.getTags().toArray(new String[0])));
        }

        inited = true;

    }

    private KafkaMessageListenerContainer<Integer, String> startContainer(int userId, MessageListener<Integer, String> listener, String... topics) {
        ContainerProperties containerProps = inited ? new ContainerProperties(getTopicPartitionsOffsets(topics))
                :  new ContainerProperties(topics);
        containerProps.setMessageListener(listener);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "main_group" + userId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer" + userId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<>(props);
        KafkaMessageListenerContainer<Integer, String> container = new KafkaMessageListenerContainer<>(cf, containerProps);

        container.setBeanName("container" + userId);
        container.start();
        return container;
    }

    private TopicPartitionOffset[] getTopicPartitionsOffsets(String... topics) {
        TopicPartitionOffset[] topicPartitions = new TopicPartitionOffset[topics.length];
        for (int i = 0; i < topics.length; i++) {
            topicPartitions[i] = new TopicPartitionOffset(topics[i], 0, TopicPartitionOffset.SeekPosition.END);
        }

        return topicPartitions;
    }

    public void newSubscription(Subscription subscription) {
        KafkaMessageListenerContainer<Integer, String> container = containers.get(subscription.getUserId());

        if (container != null) {
            container.stop();
        }

        containers.put(subscription.getUserId(), startContainer(subscription.getUserId(),
                message -> logger.info("received for user " + subscription.getUserId() + " : "  + message.value()),
                subscription.getTags().toArray(new String[0])));


    }


}
