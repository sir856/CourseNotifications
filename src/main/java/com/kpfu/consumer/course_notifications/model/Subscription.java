package com.kpfu.consumer.course_notifications.model;

import com.kpfu.consumer.course_notifications.utils.JsonArrayConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Convert(converter = JsonArrayConverter.class)
    private List<String> tags;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public List<String> getTags() {
        return tags;
    }
}
