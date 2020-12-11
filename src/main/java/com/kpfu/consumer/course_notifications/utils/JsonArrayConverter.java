package com.kpfu.consumer.course_notifications.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.List;

public class JsonArrayConverter implements AttributeConverter<List<String>, String> {
    static ObjectMapper objectMapper = new ObjectMapper();
    private final Logger log = LoggerFactory.getLogger(this.getClass());



    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        return null;
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, List.class);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
}
