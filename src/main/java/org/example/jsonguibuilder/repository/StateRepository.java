package org.example.jsonguibuilder.repository;

import java.util.Map;

/**
 * Інтерфейс для збереження станів згенерованих форм.
 */

public interface StateRepository {
    void saveState(String formName, Map<String, Object> state);

    Map<String, Object> loadLatestState(String formName);

    void clearAllStates(String formName);
}
