package ru.yandex.practicum.task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> assignedSubTasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, Status.NEW);
    }


    public void addSubTask(SubTask subTask) {
        assignedSubTasks.add(subTask);
    }

    public List<SubTask> getAssignedSubTasks() {
        return new ArrayList<>(assignedSubTasks);
    }

    public void removeSubTaskFromEpic(SubTask subTask) {
            assignedSubTasks.remove(subTask);
    }

    public void clearAssignedSubTasks() {
        assignedSubTasks.clear();
    }
}

