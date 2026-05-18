package org.example.jsonguibuilder.viewmodel;

import javafx.scene.Node;
import java.util.List;
import java.util.function.Consumer;

/**
 * Каркас Моделі Представлення (ViewModel).
 * Буде керувати станами елементів та обробкою подій від кнопок.
 */

public class MainViewModel {

    // Колбеки для зв'язку з View
    private Consumer<List<Node>> onRenderCallback;
    private Consumer<String> onErrorCallback;

    public void setOnRenderCallback(Consumer<List<Node>> callback) {
        this.onRenderCallback = callback;
    }

    public void setOnErrorCallback(Consumer<String> callback) {
        this.onErrorCallback = callback;
    }

    // Метод для зчитування файлу
    public void loadAndRenderUi(String filePath) {
        System.out.println("[ViewModel] Отримано запит на візуалізацію файлу: " + filePath);
        // логіка: виклик JsonParserService, потім JavaFxComponentFactory,
        // а потім передача списку Node у onRenderCallback.accept(nodes);
    }

    // Метод, який викликатиметься при введенні тексту чи зміні чекбоксів
    public void updateState(String componentId, Object newValue) {
        System.out.println("[ViewModel] Компонент '" + componentId + "' змінив значення на: " + newValue);
    }

    // Метод, який викликатиметься при натисканні на кнопки
    public void executeAction(String actionToken, String componentId) {
        System.out.println("[ViewModel] Кнопка '" + componentId + "' запустила подію: " + actionToken);
    }
}
