package org.example.jsonguibuilder.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.jsonguibuilder.viewmodel.MainViewModel;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainView {

    private final MainViewModel viewModel;
    private final VBox dynamicUiContainer;
    private final Label statusLabel;
    private final BorderPane root;

    public MainView(MainViewModel viewModel) {
        this.viewModel = viewModel;
        this.dynamicUiContainer = new VBox(10); // Контейнер для згенерованих елементів (відступ 10px)
        this.statusLabel = new Label("Готово до роботи. Завантажте JSON-файл.");
        this.root = new BorderPane();

        // Підключення слухачів до ViewModel: коли ViewModel згенерує UI або знайде помилку,
        // вона викличе ці методи у View
        this.viewModel.setOnRenderCallback(this::displayGeneratedUi);
        this.viewModel.setOnErrorCallback(this::displayError);
        this.viewModel.setOnLoadStateCallback(this::applyStateToUi);
        this.viewModel.setOnLoadingCallback(this::toggleLoadingState);
        this.viewModel.setOnClearSuccessCallback(this::clearUiFields);
    }

    /**
     * Побудова головного вікна програми.
     */

    public void initOwnerWindow(Stage stage) {
        root.setPadding(new Insets(10));

        // Верхня панель: кнопки керування
        ToolBar toolBar = new ToolBar();
        Button btnLoadFile = new Button("Завантажити JSON");
        Button btnSaveDb = new Button("Зберегти в БД");
        Button btnLoadDb = new Button("Завантажити з БД");
        Button btnExportFile = new Button("Експорт в JSON");
        Button btnImportFile = new Button("Імпорт з JSON");
        Button btnClearDb = new Button("Очистити БД");
        btnClearDb.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

        toolBar.getItems().addAll(btnLoadFile, btnSaveDb, btnLoadDb, btnExportFile, btnImportFile, btnClearDb);
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

        // Обробка натискання кнопки "Завантажити з БД"
        btnLoadDb.setOnAction(e -> viewModel.loadLatestStateFromDb());

        // Обробка натискання кнопки "Експорт в JSON"
        btnExportFile.setOnAction(e -> handleExportFileChooser(stage));

        // Обробка натискання кнопки "Імпорт з JSON"
        btnImportFile.setOnAction(e -> handleImportFileChooser(stage));

        // Обробка натискання кнопки "Очистити БД"
        btnClearDb.setOnAction(e -> handleClearDatabaseRequest());

        // Налаштування та запуск сцени
        Scene scene = new Scene(root, 600, 500);

        // Підключає файл стилів
        String cssPath = getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

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
     * Відкриває системне вікно збереження файлу (Save Dialog).
     */

    private void handleExportFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть місце для збереження JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        // Задає дефолтне ім'я файлу, яке користувач побачить відразу
        fileChooser.setInitialFileName("form_state_export.json");

        // Важливо: використовує showSaveDialog замість showOpenDialog
        File fileToSave = fileChooser.showSaveDialog(stage);

        if (fileToSave != null) {
            // Віддає повний шлях до файлу у ViewModel для фізичного запису
            viewModel.exportStateToJson(fileToSave.getAbsolutePath());

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Форму успішно експортовано у файл: " + fileToSave.getName());
        }
    }

    /**
     * Відкриває системне вікно вибору файлу (Open Dialog) для імпорту введених даних.
     */

    private void handleImportFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Оберіть збережений JSON файл стану");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {

            // Передає шлях у ViewModel
            viewModel.importStateFromJson(selectedFile.getAbsolutePath());

            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("Стан форми успішно імпортовано з файлу: " + selectedFile.getName());
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
     * Метод-колбек: динамічно підлаштовує дизайн вікна помилки під конкретну проблему.
     */

    private void displayError(String message) {
        statusLabel.setTextFill(Color.RED);
        statusLabel.setText("Сталася помилка виконання.");

        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Розумна фільтрація тексту для UX
        if (message.contains("MongoDB") || message.contains("бази даних")) {
            alert.setTitle("Помилка інфраструктури (СУБД)");
            alert.setHeaderText("Відсутнє підключення до бази даних!");
        } else {
            alert.setTitle("Помилка конфігурації інтерфейсу");
            alert.setHeaderText("Виявлено проблему в структурі JSON-файлу");
        }

        alert.setContentText(message);

        // Стилізує внутрішнє текстове поле для кращої читаємості
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.showAndWait();
    }

    /**
     * Метод-колбек: бере мапу з БД і автоматично заповнює активні елементи форми.
     */

    private void applyStateToUi(Map<String, Object> state) {

        // Перебирає всі графічні вузли, які зараз лежать у нашому контейнері
        for (Node node : dynamicUiContainer.getChildren()) {
            String componentId = node.getId();

            // Якщо у компонента є ID і для цього ID є дані в базі
            if (componentId != null && state.containsKey(componentId)) {
                Object value = state.get(componentId);

                // Використовує Pattern Matching для instanceof
                if (node instanceof TextField tf) {
                    tf.setText(String.valueOf(value));
                } else if (node instanceof CheckBox cb) {
                    cb.setSelected((Boolean) value);
                } else if (node instanceof RadioButton rb) {
                    rb.setSelected((Boolean) value);
                } else if (node instanceof ComboBox<?> comboBox) {
                    @SuppressWarnings("unchecked")
                    ComboBox<String> stringCombo = (ComboBox<String>) comboBox;
                    stringCombo.setValue(String.valueOf(value));
                }
            }
        }
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Стан форми успішно відновлено з MongoDB!");
    }

    /**
     * Метод-колбек: блокує або розблоковує інтерфейс під час фонових операцій.
     */

    private void toggleLoadingState(boolean isLoading) {
        // Блокуємо верхню панель (всі кнопки) та форму, щоб користувач нічого не натискав двічі
        if (root.getTop() != null) {
            root.getTop().setDisable(isLoading);
        }
        dynamicUiContainer.setDisable(isLoading);

        if (isLoading) {
            // Змінюємо курсор на "годинник" і показуємо текст
            root.setCursor(javafx.scene.Cursor.WAIT);
            statusLabel.setTextFill(Color.DARKORANGE);
            statusLabel.setText("Зв'язок із базою даних... Зачекайте ⏳");
        } else {
            // Повертаємо звичайний курсор.
            // Текст успіху чи помилки буде встановлено іншими методами (displayError або applyStateToUi),
            // тому тут ми просто скидаємо курсор.
            root.setCursor(javafx.scene.Cursor.DEFAULT);

            // Якщо це було збереження (і форма не оновлювалася), просто пишемо "Успішно"
            if (statusLabel.getText().contains("Зачекайте")) {
                statusLabel.setTextFill(Color.GREEN);
                statusLabel.setText("Операцію успішно завершено!");
            }
        }
    }

    /**
     * Запускає діалогове вікно з питанням перед видаленням.
     */

    private void handleClearDatabaseRequest() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Підтвердження видалення");
        confirmAlert.setHeaderText("Ви впевнені, що хочете очистити базу даних?");
        confirmAlert.setContentText("Усі збережені стани будуть безповоротно видалені з MongoDB. Цю дію неможливо скасувати.");

        // Показує вікно і чекаємо, яку кнопку натисне користувач
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                // Якщо натиснуто "ОК" - відправляє команду у ViewModel
                viewModel.clearDatabase();
            }
        });
    }

    /**
     * Метод-колбек: фізично прибирає текст і галочки зі згенерованих елементів.
     */

    private void clearUiFields() {
        for (Node node : dynamicUiContainer.getChildren()) {
            if (node instanceof TextField tf) {
                tf.clear();
            } else if (node instanceof CheckBox cb) {
                cb.setSelected(false);
            } else if (node instanceof RadioButton rb) {
                rb.setSelected(false);
            } else if (node instanceof ComboBox<?> comboBox) {
                comboBox.getSelectionModel().clearSelection();
            }
        }
        statusLabel.setTextFill(Color.GREEN);
        statusLabel.setText("Базу даних та форму успішно очищено!");
    }
}