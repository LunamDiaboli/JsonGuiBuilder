package org.example.jsonguibuilder.factory;

import javafx.scene.Node;
import javafx.scene.control.*;
import org.example.jsonguibuilder.model.ComponentConfig;
import org.example.jsonguibuilder.viewmodel.MainViewModel;

import java.util.List;

public class JavaFxComponentFactory implements UiComponentFactory {

    @Override
    public Node createComponent(ComponentConfig config, MainViewModel viewModel) {

        // switch повертає значення прямо у змінну control
        Control control = switch (config.type().toLowerCase()) {
            case "button" -> createButton(config, viewModel);
            case "textfield" -> createTextField(config, viewModel);
            case "combobox" -> createComboBox(config, viewModel);
            case "checkbox" -> createCheckBox(config, viewModel);
            case "radiobutton" -> createRadioButton(config, viewModel);
            default -> new Label("Невідомий тип компонента: " + config.type());
        };

        // Застосування розмірів, якщо вони вказані в JSON
        if (config.width() != null && config.width() > 0) {
            control.setPrefWidth(config.width());
        }
        if (config.height() != null && config.height() > 0) {
            control.setPrefHeight(config.height());
        }

        return control;
    }

    private Button createButton(ComponentConfig config, MainViewModel viewModel) {
        Button button = new Button(config.text());

        // Якщо в JSON вказано токен дії, прив'язуємо клік до ViewModel
        if (config.action() != null && !config.action().isBlank()) {
            button.setOnAction(event -> viewModel.executeAction(config.action(), config.id()));
        }
        return button;
    }

    private TextField createTextField(ComponentConfig config, MainViewModel viewModel) {
        TextField textField = new TextField(config.text());

        // Слухаємо зміни тексту в реальному часі та передаємо у ViewModel
        textField.textProperty().addListener((observable, oldValue, newValue) ->
                viewModel.updateState(config.id(), newValue)
        );
        return textField;
    }

    @SuppressWarnings("unchecked")
    private ComboBox<String> createComboBox(ComponentConfig config, MainViewModel viewModel) {
        ComboBox<String> comboBox = new ComboBox<>();

        // Витягуємо список елементів з мапи properties
        if (config.properties() != null && config.properties().containsKey("items")) {
            Object itemsObj = config.properties().get("items");
            if (itemsObj instanceof List) {
                comboBox.getItems().addAll((List<String>) itemsObj);
            }
        }

        // Слухаємо, який елемент обрав користувач
        comboBox.valueProperty().addListener((observable, oldValue, newValue) ->
                viewModel.updateState(config.id(), newValue)
        );
        return comboBox;
    }

    private CheckBox createCheckBox(ComponentConfig config, MainViewModel viewModel) {
        CheckBox checkBox = new CheckBox(config.text());

        if (config.properties() != null && config.properties().containsKey("selected")) {
            checkBox.setSelected((Boolean) config.properties().get("selected"));
        }

        // Слухаємо перемикання прапорця
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) ->
                viewModel.updateState(config.id(), newValue)
        );
        return checkBox;
    }

    private RadioButton createRadioButton(ComponentConfig config, MainViewModel viewModel) {
        RadioButton radioButton = new RadioButton(config.text());

        if (config.properties() != null && config.properties().containsKey("selected")) {
            radioButton.setSelected((Boolean) config.properties().get("selected"));
        }

        // Слухаємо перемикання радіокнопки
        radioButton.selectedProperty().addListener((observable, oldValue, newValue) ->
                viewModel.updateState(config.id(), newValue)
        );
        return radioButton;
    }
}