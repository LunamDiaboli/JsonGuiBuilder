package org.example.jsonguibuilder;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.jsonguibuilder.view.MainView;
import org.example.jsonguibuilder.viewmodel.MainViewModel;

/**
 * Головний клас запуску програми.
 * Керування життєвим циклом графічної платформи JavaFX.
 */

public class JsonGuiBuilderApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Ініціалізування шару логіки та керування станами (ViewModel)
        MainViewModel viewModel = new MainViewModel();

        // Ініціалізування шару представлення (View)
        MainView view = new MainView(viewModel);

        // Будування та відображення головного вікна конструктора
        view.initOwnerWindow(primaryStage);
    }

    public static void main(String[] args) {

        // Запуск підсистеми JavaFX
        launch(args);
    }
}
