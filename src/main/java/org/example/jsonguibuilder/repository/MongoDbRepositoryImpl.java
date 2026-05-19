package org.example.jsonguibuilder.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Map;

public class MongoDbRepositoryImpl implements StateRepository {

    // Рядок підключення з docker-compose.yml
    private static final String CONNECTION_STRING = "mongodb://admin:password@localhost:27017";
    private static final String DATABASE_NAME = "jsonguibuilder_db";
    private static final String COLLECTION_NAME = "form_states";

    @Override
    public void saveState(String formName, Map<String, Object> state) {

        // Використовує конструкцію try-with-resources, щоб з'єднатись з БД
        // автоматично закривається після завершення операції (захист від витоку пам'яті)
        try (MongoClient mongoClient = MongoClients.create(CONNECTION_STRING)) {

            // Отримання доступу до бази та колекції-таблиці
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            // Перетворює Java Map у BSON-документ
            Document document = new Document();
            document.put("formName", formName);
            document.put("savedAt", System.currentTimeMillis());

            // Вкладає всі введені користувачем дані як піддокумент
            document.put("uiData", new Document(state));

            // Відправляє документ у базу даних
            collection.insertOne(document);
            System.out.println("[MongoDB] Стан форми успішно збережено в колекцію " + COLLECTION_NAME);

        } catch (Exception e) {
            System.err.println("[MongoDB Помилка] Не вдалося зберегти стан: " + e.getMessage());
            throw new RuntimeException("Помилка збереження в БД: " + e.getMessage());
        }
    }
}