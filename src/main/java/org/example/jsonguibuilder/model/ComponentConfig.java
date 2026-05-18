package org.example.jsonguibuilder.model;

import java.util.Map;

/**
 * Data Transfer Object
 */

public record ComponentConfig(
        String id,
        String type,
        String text,
        Integer width,
        Integer height,
        Map<String, Object> properties,
        String action
) {

    /**
     * Constructor for validating data during its creation.
     * If JSON does not contain mandatory fields, the system will immediately throw out an error.
     */

    public ComponentConfig {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Помилка: Кожен компонент у JSON повинен мати унікальний 'id'.");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Помилка: Для компонента з id '" + id + "' не вказано обов'язкове поле 'type'.");
        }
    }
}
