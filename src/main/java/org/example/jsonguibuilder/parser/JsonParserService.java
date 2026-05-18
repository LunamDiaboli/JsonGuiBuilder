package org.example.jsonguibuilder.parser;

import org.example.jsonguibuilder.model.ComponentConfig;

import java.util.List;

/**
 * Інтерфейс для сервісу парсингу та валідації JSON конфігурацій.
 */

public interface JsonParserService {
    // Перетворює текст JSON на список об'єктів конфігурації
    List<ComponentConfig> parseJson(String jsonContent);

    // Перевіряє, чи не пошкоджений JSON та чи немає дублікатів ID
    boolean validateSchema(String jsonContent);
}
