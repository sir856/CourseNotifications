package com.kpfu.consumer.course_notifications.components;

import com.kpfu.consumer.course_notifications.model.Subscription;
import com.kpfu.consumer.course_notifications.repository.SubscriptionRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
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


/**
 * Компонет, который отвечает за создание и управление потребителями в Apache Kafka
 *
 * @author Ильфат Саяхов
 */
@Component
public class ConsumersComponent {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<Integer, KafkaMessageListenerContainer<Integer, String>> containers = new HashMap<>();
    private final SubscriptionRepository subscriptionRepository;
    private boolean initialized = false;

    /**
     * Конструктор для инъекции {@link SubscriptionRepository}
     *
     * @param subscriptionRepository
     */
    @Autowired
    public ConsumersComponent(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }


    /**
     * Метод, который выполняется один раз, при старте сервера. В методе происходит запуск потребителей, сохраненных в базе данных.
     * Для подключения к Apache Kafka потребителя создается контейнер {@link ConsumersComponent#startContainer(int, MessageListener, String...)}, с помощью которого можно управлять полученным подключением.
     *
     */
    @PostConstruct
    public void init() {
        logger.info("Start init containers");

        List<Subscription> subscriptions = subscriptionRepository.findAll();

        for (Subscription subscription : subscriptions) {
            containers.put(subscription.getUserId(), startContainer(subscription.getUserId(),
                    message -> logger.info("received for user " + subscription.getUserId() + " : "  + message.value()),
                    subscription.getTags().toArray(new String[0])));
        }

        initialized = true;
    }

    /**
     * Метод который создает и запускает контейнер для потребителя в Apache Kafka
     *
     * @param userId - id пользователя, для которого запускается контейнер
     * @param listener - функция, которая выполяется при получении сообщения
     * @param topics - топики, на которые нужно подписаться
     * @return запущенный контейнер
     */

    private KafkaMessageListenerContainer<Integer, String> startContainer(int userId, MessageListener<Integer, String> listener, String... topics) {
        ContainerProperties containerProps = initialized ? new ContainerProperties(getTopicPartitionsOffsets(topics))
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

    /**
     * Метод, который преобразует массив топиков, в массив топиков со смещением к концу
     *
     * @param topics
     * @return массив топиков со смещением
     */

    private TopicPartitionOffset[] getTopicPartitionsOffsets(String... topics) {
        TopicPartitionOffset[] topicPartitions = new TopicPartitionOffset[topics.length];
        for (int i = 0; i < topics.length; i++) {
            topicPartitions[i] = new TopicPartitionOffset(topics[i], 0, TopicPartitionOffset.SeekPosition.END);
        }

        return topicPartitions;
    }

    /**
     * Метод, который добавляет нового потребителя с id {@link Subscription#getUserId()} и подписывает его на топики с названиями из списка {@link Subscription#getTags()}.
     * Если потребитель с таким id уже существует, то его подписки меняются на новые
     *
     * @param subscription - описание потребителя
     * @see Subscription
     */

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
