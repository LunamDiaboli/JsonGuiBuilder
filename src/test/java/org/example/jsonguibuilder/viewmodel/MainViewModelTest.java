package org.example.jsonguibuilder.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MainViewModelTest {

    private MainViewModel viewModel;

    // Метод викликається переж кожним тестом, щоб ми завжди мали "чистий" стан
    @BeforeEach
    void setUp() {
        viewModel = new MainViewModel();
    }

    @Test
    @DisplayName("Позитивний тест: Збереження введених даних (updateState)")
    void shouldUpdateStateCorrectly() {
        // ARRANGE & ACT: Імітує введення користувачем імені у поле "txtName"
        viewModel.updateState("txtName", "Денис");

        // Додає ще одне поле "age"
        viewModel.updateState("age", 20);

        // ASSERT: Перевіряє, чи спрацює помилка експорту, якщо форма не порожня.
        // Підписується на колбек помилки.
        AtomicReference<String> errorMessage = new AtomicReference<>();
        viewModel.setOnErrorCallback(errorMessage::set);

        // Спробує експортувати дані
        // Якщо даних немає, ViewModel кине в колбек повідомлення "Немає даних для експорту".
        // Якщо дані є, ViewModel спробує зберегти файл, але помилка, якщо і буде,
        // стосуватиметься саме файлової системи (через неправильний шлях), а не порожньої форми.

        viewModel.exportStateToJson("dummy/path.json");

        // Якщо помилка не містить слова "Немає даних", отже, дані успішно потрапили у пам'ять
        if (errorMessage.get() != null) {
            assertFalse(errorMessage.get().contains("Немає даних"),
                    "Дані не збереглися у внутрішньому стані uiState");
        }
    }

    @Test
    @DisplayName("Негативний тест: Експорт порожньої форми (має викликати помилку)")
    void shouldTriggerErrorWhenExportingEmptyState() {
        // ARRANGE: Форма абсолютно порожня (не викликали updateState)

        // AtomicReference - це обгортка, яка дозволяє зберегти текст, який прийде зсередини колбеку
        AtomicReference<String> caughtErrorMessage = new AtomicReference<>();

        // Підписуємося на сигнали ViewModel
        viewModel.setOnErrorCallback(caughtErrorMessage::set);

        // ACT: Намагаємся експортувати
        viewModel.exportStateToJson("test.json");

        // ASSERT: Перевіряє, що ViewModel не промовчала, а надіслала сигнал помилки
        assertNotNull(caughtErrorMessage.get(), "ViewModel мала відправити повідомлення про помилку");

        // Перевіряє, що текст помилки правильний
        assertTrue(caughtErrorMessage.get().contains("Немає даних для експорту"),
                "Текст помилки не відповідає очікуваному");
    }

    @Test
    @DisplayName("Негативний тест: Імпорт неіснуючого файлу JSON")
    void shouldTriggerErrorWhenImportingInvalidFile() {
        // ARRANGE: Підписуємося на помилки
        AtomicReference<String> caughtErrorMessage = new AtomicReference<>();
        viewModel.setOnErrorCallback(caughtErrorMessage::set);

        // ACT: Намагаємося імпортувати файл, якого точно не існує
        viewModel.importStateFromJson("file_that_does_not_exist_999.json");

        // ASSERT: ViewModel має зловити системну помилку (NoSuchFileException) і передати її у View
        assertNotNull(caughtErrorMessage.get(), "ViewModel мала відловити помилку файлової системи");
        assertTrue(caughtErrorMessage.get().contains("Не вдалося імпортувати"),
                "Помилка має повідомляти про проблему з імпортом");
    }
}