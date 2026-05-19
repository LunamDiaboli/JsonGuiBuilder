package org.example.jsonguibuilder.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.jsonguibuilder.viewmodel.MainViewModel;

import java.io.File;
import java.util.List;

public class MainView {

    private final MainViewModel viewModel;
    private final VBox dynamicUiContainer;
    private final Label statusLabel;

    public MainView(MainViewModel viewModel) {
        this.viewModel = viewModel;
        this.dynamicUiContainer = new VBox(10); // Контейнер для згенерованих елементів (відступ 10px)
        this.statusLabel = new Label("Готово до роботи. Завантажте JSON-файл.");

        // Підключення слухачів до ViewModel: коли ViewModel згенерує UI або знайде помилку,
        // вона викличе ці методи у View
        this.viewModel.setOnRenderCallback(this::displayGeneratedUi);
        this.viewModel.setOnErrorCallback(this::displayError);
    }

    /**
     * Побудова головного вікна програми.
     */

    public void initOwnerWindow(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Верхня панель: кнопки керування
        ToolBar toolBar = new ToolBar();
        Button btnLoadFile = new Button("Завантажити JSON");
        Button btnSaveDb = new Button("Зберегти в БД");
        toolBar.getItems().addAll(btnLoadFile, btnSaveDb);
        root.setTop(toolBar);

        // Центральна панель: місце, де буде малюватися згенерований інтерфейс
        dynamicUiContainer.setPadding(new Insets(15));
        dynamicUiContainer.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = new ScrollPane(dynamicUiContainer);
        scrollPane.setFitToWidth(true); // Форма розтягуватиметься по ширині
        root.setCenter(scrollPane);

        // Нижня панель: статус-бар
        statusLabel.setTextFill(Color.DARKBLUE);
        root.setBottom(statusLabel);

        // Обробка натискання кнопки "Завантажити JSON"
        btnLoadFile.setOnAction(e -> handleOpenFileChooser(stage));

        // Обробка натискання кнопки "Зберегти в БД"
        btnSaveDb.setOnAction(e -> viewModel.saveCurrentState());

        // Налаштування та запуск сцени
        Scene scene = new Scene(root, 600, 500);
        stage.setTitle("JSON GUI Builder - Панель розробника");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Відкривання системного вікна вибору файлу.
     */

    private void handleOpenFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть JSON файл конфігурації");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            statusLabel.setTextFill(Color.DARKBLUE);
            statusLabel.setText("Обробка файлу: " + selectedFile.getName());

            // Передаємо шлях до файлу у ViewModel для парсингу
            viewModel.loadAndRenderUi(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Метод-колбек: очищає старий інтерфейс і малює новий.
     */

    private void displayGeneratedUi(List<Node> nodes) {
        dynamicUiContainer.getChildren().clear();
        dynamicUiContainer.getChildren().addAll(nodes);
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Інтерфейс успішно згенеровано!");
    }

    /**
     * Метод-колбек: показує вікно, що спливає, з помилкою якщо JSON зламаний.
     */

    private void displayError(String message) {
        statusLabel.setTextFill(Color.RED);
        statusLabel.setText("Помилка генерації.");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка JSON");
        alert.setHeaderText("Виявлено проблему в конфігурації");
        alert.setContentText(message);
        alert.showAndWait();
    }
}