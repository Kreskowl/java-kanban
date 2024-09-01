package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void clearHistory();
}
