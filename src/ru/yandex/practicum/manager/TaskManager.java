package ru.yandex.practicum.manager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int currentId = 1;

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearAssignedSubTasks();
            updateEpicStatus(epic);
        }
    }

    public void clearEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void createNewTask(Task task) {
        task.setId(currentId);
        tasks.put(currentId, task);
        currentId++;
    }

    public void createNewSubTask(SubTask subTask) {
        subTask.setId(currentId);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
            subTasks.put(currentId, subTask);
            currentId++;
        } else {
            return;
        }
    }

    public void createNewEpic(Epic epic) {
        epic.setId(currentId);
        epics.put(currentId, epic);
        currentId++;
    }

    public ArrayList<SubTask> getAssignedSubTasks(int id) {
        return epics.get(id).getAssignedSubTasks(epics.get(id));
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        SubTask subTaskToDelete = subTasks.remove(id);
        Epic masterEpic = epics.get(subTaskToDelete.getEpicId());
        masterEpic.removeSubTaskFromEpic(subTaskToDelete);
        updateEpicStatus(epics.get(subTaskToDelete.getEpicId()));
    }

    public void deleteEpicById(int id) {
        epics.remove(id);
        subTasks.keySet().removeIf(subTaskId -> subTasks.get(subTaskId).getEpicId() == id);
    }

    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();
        tasks.put(id, updatedTask);
    }

    public void updateSubTask(SubTask updatedSubTask) {
        int id = updatedSubTask.getId();
        Epic masterEpic = epics.get(updatedSubTask.getEpicId());

        SubTask oldSubtask = subTasks.get(id);
        masterEpic.removeSubTaskFromEpic(oldSubtask);
        masterEpic.addSubTask(updatedSubTask);
        subTasks.put(id, updatedSubTask);
        updateEpicStatus(epics.get(updatedSubTask.getEpicId()));
    }

    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        Epic oldEpic = epics.get(id);
        ArrayList<SubTask> existingSubTasks = oldEpic.getAssignedSubTasks(oldEpic);

        for (SubTask subTask : existingSubTasks) {
            updatedEpic.addSubTask(subTask);
        }
        epics.put(id, updatedEpic);
    }

    private static void updateEpicStatus(Epic epic) {
        ArrayList<SubTask> checkStatus = epic.getAssignedSubTasks(epic);

        if (checkStatus.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : checkStatus) {
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}


