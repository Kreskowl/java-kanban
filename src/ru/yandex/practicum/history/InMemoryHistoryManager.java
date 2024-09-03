package ru.yandex.practicum.history;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();
    private final static int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (isNotNull(task)) {
            if (isFull()) {
                history.removeFirst();
                history.add(history.size(), task);
            } else {
                history.add(task);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }

    private boolean isFull() {
        return history.size() >= MAX_SIZE;
    }

    private boolean isNotNull(Task task) {
        return task != null;
    }
}