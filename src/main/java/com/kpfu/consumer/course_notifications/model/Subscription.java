package com.kpfu.consumer.course_notifications.model;

import com.kpfu.consumer.course_notifications.utils.JsonArrayConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Класс предоставляющий информацию о подписках пользователя
 *
 * @author Ильфат Саяхов
 */
@Entity
@Table(name = "subscription")
public class Subscription implements Serializable {

    @Id
    @Column(name = "user_id")
    private int userId;

    /**
     * Список, в котором хранятся названия тегов, на которые подписан поьзователь.
     * Для преобразования из JSON-ARRAY в список и обратно используется класс {@link JsonArrayConverter}
     */
    @Convert(converter = JsonArrayConverter.class)
    private List<String> tags;


    public int getUserId() {
        return userId;
    }

    public List<String> getTags() {
        return tags;
    }
}
