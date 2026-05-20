package org.example.jsonguibuilder.viewmodel;

import javafx.application.Platform;
import javafx.scene.Node;
import org.example.jsonguibuilder.factory.JavaFxComponentFactory;
import org.example.jsonguibuilder.factory.UiComponentFactory;
import org.example.jsonguibuilder.model.ComponentConfig;
import org.example.jsonguibuilder.parser.GsonParserServiceImpl;
import org.example.jsonguibuilder.parser.JsonParserService;
import org.example.jsonguibuilder.repository.MongoDbRepositoryImpl;
import org.example.jsonguibuilder.repository.StateRepository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Каркас Моделі Представлення (ViewModel).
 * Буде керувати станами елементів та обробкою подій від кнопок.
 */

public class MainViewModel {

    // Репозиторій
    private final StateRepository stateRepository;

    // Сервіси системи
    private final JsonParserService parserService;
    private final UiComponentFactory uiFactory;

    // Сховище станів для введених користувачем даних
    private final Map<String, Object> uiState;

    // Колбеки для зв'язку з View
    private Consumer<List<Node>> onRenderCallback;
    private Consumer<String> onErrorCallback;
    private Consumer<Map<String, Object>> onLoadStateCallback;
    private Consumer<Boolean> onLoadingCallback;

    public void setOnLoadingCallback(Consumer<Boolean> callback) {
        this.onLoadingCallback = callback;
    }

    public void setOnLoadStateCallback(Consumer<Map<String, Object>> callback) {
        this.onLoadStateCallback = callback;
    }

    public MainViewModel() {
        this.parserService = new GsonParserServiceImpl();
        this.uiFactory = new JavaFxComponentFactory();
        this.uiState = new HashMap<>();
        this.stateRepository = new MongoDbRepositoryImpl();
    }

    // Безпечно передає стан завантаження у головний потік JavaFX.

    private void notifyLoading(boolean isLoading) {
        if (onLoadingCallback != null) {
            Platform.runLater(() -> onLoadingCallback.accept(isLoading));
        }
    }

    public void setOnRenderCallback(Consumer<List<Node>> callback) {
        this.onRenderCallback = callback;
    }

    public void setOnErrorCallback(Consumer<String> callback) {
        this.onErrorCallback = callback;
    }

    /**
     * Основна логіка: читання файлу -> парсинг -> генерація UI -> відправлення на екран
     */

    // Метод для зчитування файлу
    public void loadAndRenderUi(String filePath) {
        try {
            // Зчитує текст із файлу
            String jsonRaw = Files.readString(Paths.get(filePath));

            // Робить парсинг JSON в об'єкти (DTO)
            List<ComponentConfig> componentConfigs = parserService.parseJson(jsonRaw);

            // Очищає попередній стан пам'яті
            uiState.clear();

            // Фабрика перетворює конфігурації на справжні елементи JavaFX
            List<Node> generatedNodes = new ArrayList<>();
            for (ComponentConfig config : componentConfigs) {
                Node node = uiFactory.createComponent(config, this);
                generatedNodes.add(node);
            }

            // Передає готові вузли назад у вікно
            if (onRenderCallback != null) {
                onRenderCallback.accept(generatedNodes);
            }

        } catch (Exception ex) {

            // Якщо JSON зламаний або файлу немає — викликає вікно з помилкою
            if (onErrorCallback != null) {
                onErrorCallback.accept(ex.getMessage());
            }
        }
    }

    // Метод, який викликатиметься при введенні тексту чи зміні чекбоксів
    public void updateState(String componentId, Object newValue) {
        uiState.put(componentId, newValue);
        System.out.println("[ViewModel - Стан] Поле '" + componentId + "' збережено в пам'ять: " + newValue);
    }

    // Метод, який викликатиметься при натисканні на кнопки
    public void executeAction(String actionToken, String componentId) {
        System.out.println("[ViewModel - Подія] Натиснуто кнопку '" + componentId + "'. Дія: " + actionToken);
        System.out.println("Поточний стан форми у пам'яті: " + uiState);
    }

    /**
     * АСИНХРОННЕ збереження стану в MongoDB.
     */

    public void saveCurrentState() {
        if (uiState.isEmpty()) {
            if (onErrorCallback != null) {
                onErrorCallback.accept("Немає даних для збереження. Заповніть форму.");
            }
            return;
        }

        // Вмикає індикатор завантаження
        notifyLoading(true);

        System.out.println("[ViewModel] Даємо команду фоновому потоку на збереження...");

        // CompletableFuture запускає важку роботу в іншому (фоновому) потоці
        CompletableFuture.runAsync(() -> {
            try {
                // Штучна затримка на 1 секунду для імітації довгої роботи мережі (для тесту)
                Thread.sleep(1000);

                stateRepository.saveState("DynamicForm_v1", uiState);

                Platform.runLater(() -> {

                    // Вимикає індикатор після успіху
                    notifyLoading(false);

                    // Щоб показати повідомлення про успіх, можемо використати onErrorCallback або створити новий.
                    // Для простоти виведемо в консоль (View сама покаже успіх через statusLabel).
                    System.out.println("[Головний потік] Збереження успішне.");
                });

                System.out.println("[Фоновий потік] Дані успішно відправлено до бази.");

            } catch (Exception e) {

                // Якщо сталася помилка (наприклад, БД вимкнена), ми повинні повернутися
                // в головний потік (Platform.runLater), щоб показати вікно помилки
                Platform.runLater(() -> {
                    notifyLoading(false);
                    if (onErrorCallback != null) {
                        onErrorCallback.accept(e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * АСИНХРОННЕ завантаження стану форми з MongoDB.
     */

    public void loadLatestStateFromDb() {

        // Вмикає індикатор завантаження
        notifyLoading(true);

        System.out.println("[ViewModel] Даємо команду фоновому потоку на завантаження...");

        CompletableFuture.runAsync(() -> {
            try {
                // Штучна затримка на 1 секунду
                Thread.sleep(1000);

                // Забирає дані
                Map<String, Object> latestState = stateRepository.loadLatestState("DynamicForm_v1");

                // Коли дані готові, повертаємось до головного потоку, щоб він оновив UI
                Platform.runLater(() -> {

                    // Вимикає індикатор після успіху
                    notifyLoading(false);
                    if (latestState.isEmpty()) {
                        if (onErrorCallback != null) {
                            onErrorCallback.accept("У базі даних ще немає збережених станів для цієї форми.");
                        }
                        return;
                    }

                    uiState.clear();
                    uiState.putAll(latestState);

                    if (onLoadStateCallback != null) {
                        onLoadStateCallback.accept(latestState);
                    }
                    System.out.println("[Головний потік] UI успішно оновлено відновленими даними.");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {

                    // Вимикає індикатор після успіху
                    notifyLoading(false);
                    if (onErrorCallback != null) {
                        onErrorCallback.accept(e.getMessage());
                    }
                });
            }
        });
    }
}
