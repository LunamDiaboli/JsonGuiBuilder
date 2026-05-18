module org.example.jsonguibuilder {
    requires javafx.controls;

    requires com.google.gson;

    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens org.example.jsonguibuilder to javafx.controls;

    opens org.example.jsonguibuilder.model to com.google.gson;

    exports org.example.jsonguibuilder;
}