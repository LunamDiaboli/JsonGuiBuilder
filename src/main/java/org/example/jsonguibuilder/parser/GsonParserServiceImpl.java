package org.example.jsonguibuilder.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.example.jsonguibuilder.model.ComponentConfig;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GsonParserServiceImpl implements JsonParserService {

    private final Gson gson;

    public GsonParserServiceImpl() {
        // Ініціалізує рушій Gson
        this.gson = new Gson();
    }

    @Override
    public List<ComponentConfig> parseJson(String jsonContent) throws JsonSyntaxException {
        // Захист від порожніх файлів
        if (jsonContent == null || jsonContent.isBlank()) {
            return new ArrayList<>();
        }

        // В Java типи стираються (Type Erasure),
        // Явне вказування Gson для отримання саме List<ComponentConfig>
        Type listType = new TypeToken<ArrayList<ComponentConfig>>() {}.getType();

        try {
            // Перетворення тексту на об'єкти
            return gson.fromJson(jsonContent, listType);
        } catch (JsonSyntaxException e) {
            // Перехоплення помилки, якщо в JSON пропущена кома чи дужка
            throw new JsonSyntaxException("Невалідний синтаксис JSON: " + e.getMessage());
        }
    }

    @Override
    public boolean validateSchema(String jsonContent) {
        try {
            List<ComponentConfig> configs = parseJson(jsonContent);

            // Перевірка логічної цілісності: усі ID мають бути унікальними
            long uniqueIds = configs.stream().map(ComponentConfig::id).distinct().count();
            return uniqueIds == configs.size();

        } catch (Exception e) {
            return false; // Якщо файл зламаний, схема невалідна
        }
    }
}