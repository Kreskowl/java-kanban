package ru.yandex.practicum.task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> assignedSubTasks = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, Status.NEW);
    }


    public void addSubTask(SubTask subTask) {
        assignedSubTasks.add(subTask);
    }

    public ArrayList<SubTask> getAssignedSubTasks(Epic epic) {
        ArrayList<SubTask> subtasks = new ArrayList<>();
        for (SubTask subTask : assignedSubTasks) {
            subtasks.add(subTask);
        }
        return subtasks;
    }

    public void updateSubTask(int index, SubTask updatedSubTask) {
        assignedSubTasks.set(index, updatedSubTask);
    }

    public void removeSubTaskFromEpic(SubTask subTask) {
            assignedSubTasks.remove(subTask);
    }

    public void clearAssignedSubTasks() {
        assignedSubTasks.clear();
    }
}

