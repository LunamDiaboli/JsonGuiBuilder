module org.example.jsonguibuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.jsonguibuilder to javafx.fxml;
    exports org.example.jsonguibuilder;
}