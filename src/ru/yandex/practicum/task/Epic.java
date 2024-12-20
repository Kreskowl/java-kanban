package ru.yandex.practicum.task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> assignedSubTasks = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.type = TasksTypes.EPIC;
    }

    public Epic(String name, String description) {
        super(name, description);
        this.type = TasksTypes.EPIC;
    }

    public void addSubTask(SubTask subTask) {
        assignedSubTasks.add(subTask);
    }

    public List<SubTask> getAssignedSubTasks() {
        return assignedSubTasks.stream().toList();
    }

    public void removeSubTaskFromEpic(SubTask subTask) {
        assignedSubTasks.remove(subTask);
    }

    public void clearAssignedSubTasks() {
        assignedSubTasks.clear();
    }
}

