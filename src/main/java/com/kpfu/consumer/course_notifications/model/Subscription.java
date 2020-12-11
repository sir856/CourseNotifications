package com.kpfu.consumer.course_notifications.model;

import com.kpfu.consumer.course_notifications.utils.JsonArrayConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "subscription")
public class Subscription implements Serializable {

    @Id
    @Column(name = "user_id")
    private int userId;

    @Convert(converter = JsonArrayConverter.class)
    private List<String> tags;


    public int getUserId() {
        return userId;
    }

    public List<String> getTags() {
        return tags;
    }
}
