package ru.yandex.practicum.util;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.history.InMemoryHistoryManager;
import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.manager.ManagerSaveException;
import ru.yandex.practicum.manager.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedManager() {
        File defaultSaveFilePath = new File("src/ru/yandex/practicum/resources/savedTasks.txt");
        try {
            if (!defaultSaveFilePath.exists() || Files.size(defaultSaveFilePath.toPath()) == 0) {
                return new FileBackedTaskManager(getDefaultHistory(), defaultSaveFilePath);
            } else {
                return FileBackedTaskManager.loadFromFile(defaultSaveFilePath);
            }
        } catch (IOException fileNotFound) {
            throw new ManagerSaveException("Can`t load the file " + defaultSaveFilePath.getAbsolutePath());
        }
    }
}
