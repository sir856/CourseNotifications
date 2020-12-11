package com.kpfu.consumer.course_notifications.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.List;

public class JsonArrayConverter implements AttributeConverter<List<String>, String> {
    static ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());



    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        try {
            return objectMapper.writeValueAsString(strings);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
            return null;
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s, List.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }
}
