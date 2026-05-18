package org.example.jsonguibuilder.viewmodel;

/**
 * Каркас Моделі Представлення (ViewModel).
 * Буде керувати станами елементів та обробкою подій від кнопок.
 */

public class MainViewModel {

    // Метод, який викликатиметься при введенні тексту чи зміні чекбоксів
    public void updateState(String componentId, Object newValue) {
        System.out.println("[ViewModel] Компонент '" + componentId + "' змінив значення на: " + newValue);
    }

    // Метод, який викликатиметься при натисканні на кнопки
    public void executeAction(String actionToken, String componentId) {
        System.out.println("[ViewModel] Кнопка '" + componentId + "' запустила подію: " + actionToken);
    }
}
