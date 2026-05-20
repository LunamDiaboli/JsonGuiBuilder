package org.example.jsonguibuilder.parser;

import org.example.jsonguibuilder.model.ComponentConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

// Імпортує методи Assertions для перевірок
import static org.junit.jupiter.api.Assertions.*;

class GsonParserServiceImplTest {

    // Створює екземпляр класу, який буде тестуватися
    private final JsonParserService parserService = new GsonParserServiceImpl();

    @Test
    @DisplayName("Позитивний тест: Парсинг валідного JSON з одним компонентом")
    void shouldParseValidJsonCorrectly() {

        // ARRANGE (Підготовка)
        // Пише правильний JSON-рядок так, ніби зчитався з файлу
        String validJson = "[\n" +
                "  {\n" +
                "    \"id\": \"test_btn\",\n" +
                "    \"type\": \"Button\",\n" +
                "    \"text\": \"Натисни мене\"\n" +
                "  }\n" +
                "]";

        // ACT (Дія)
        // Запускає парсер
        List<ComponentConfig> result = parserService.parseJson(validJson);

        // ASSERT (Перевірка)
        // Перевіряє, чи парсер не повернув null
        assertNotNull(result, "Список компонентів не повинен бути null");

        // Перевіряє, чи в списку рівно 1 елемент (бо передали один об'єкт)
        assertEquals(1, result.size(), "Розмір списку має дорівнювати 1");

        // Дістає цей перший елемент і перевіряє його поля
        ComponentConfig firstComponent = result.get(0);
        assertEquals("test_btn", firstComponent.id(), "ID компонента розпарсився неправильно");
        assertEquals("Button", firstComponent.type(), "Тип компонента розпарсився неправильно");
        assertEquals("Натисни мене", firstComponent.text(), "Текст компонента розпарсився неправильно");
    }

    @Test
    @DisplayName("Негативний тест: Реакція парсеру на зламаний JSON")
    void shouldThrowExceptionWhenJsonIsInvalid() {
        // ARRANGE (Підготовка)
        // Пишемо відверто зламаний JSON (без дужки, що закриває і лапок)
        String invalidJson = "[ { \"id\": \"btn1\", \"type\": bad_value } ";

        // ACT & ASSERT (Дія та Перевірка одночасно)
        // Очікуємо, що програма викине помилку RuntimeException.
        // Якщо вона її викине — тест ПРОЙДЕНО. Якщо програма спробує це "проковтнути" — тест ПРОВАЛЕНО.
        Exception exception = assertThrows(RuntimeException.class, () -> {
            parserService.parseJson(invalidJson);
        });

        // Виводить реальний текст помилки в консоль, щоб бачити, що саме відповідає програма
        System.out.println("Очікувана помилка відловлена успішно! Текст: " + exception.getMessage());

        // Перевіряє просто факт того, що помилка не порожня
        assertNotNull(exception.getMessage(), "Повідомлення про помилку не повинно бути порожнім");
    }
}
