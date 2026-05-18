package org.example.jsonguibuilder.factory;

import javafx.scene.Node;
import org.example.jsonguibuilder.model.ComponentConfig;
import org.example.jsonguibuilder.viewmodel.MainViewModel;

/**
 * Інтерфейс фабрики для динамічного створення графічних компонентів.
 */

public interface UiComponentFactory {
    // Метод приймає конфігурацію і посилання на ViewModel, а повертає готовий віджет JavaFX
    Node createComponent(ComponentConfig config, MainViewModel viewModel);
}
