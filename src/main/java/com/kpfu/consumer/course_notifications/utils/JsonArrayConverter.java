package com.kpfu.consumer.course_notifications.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;


/**
 * Класс, который предназначен для преобразования строки JSON-ARRAY в список строк и обратно
 *
 * @author Ильфат Саяхов
 */
public class JsonArrayConverter implements AttributeConverter<List<String>, String> {
    static ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Преобразует список строк в строку JSON-ARRAY
     *
     * @param strings - список строк
     * @return строка или null если преобразование не удалось
     */
    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        try {
            return objectMapper.writeValueAsString(strings);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
            return null;
        }
    }

    /**
     * Преобразует строку JSON-ARRAY в список строк
     *
     * @param s - строка JSON-ARRAY
     * @return список строк или null если преобразование не удалось
     */
    @Override
    public List<String> convertToEntityAttribute(String s) {
        try {
            return convertToStringList(objectMapper.readValue(s, List.class));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Преобразует список объектов в список строк
     *
     * @param list - список объектов
     * @return список строк
     */

    private List<String> convertToStringList(List list) {
        List<String> stringList = new ArrayList<>();
        for (Object item : list) {
            stringList.add(item.toString());
        }
        return stringList;
    }
}
