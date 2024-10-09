package ru.yandex.practicum.manager;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.history.InMemoryHistoryManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class Managers {


    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBackedManager() {
        File defaultSaveFilePath = new File("src/ru/yandex/practicum/resources/savedTasks.txt");
        try {
            if (!defaultSaveFilePath.exists() || Files.size(defaultSaveFilePath.toPath()) == 0) {
                return new FileBackedTaskManager(defaultSaveFilePath);
            } else {
                return FileBackedTaskManager.loadFromFile(defaultSaveFilePath);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Can`t load the file " + defaultSaveFilePath.getPath());
        }
    }
}
