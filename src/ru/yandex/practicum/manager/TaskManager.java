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
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<SubTask> getSubTasksList() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            subTasksList.add(subTask);
        }
        return subTasksList;
    }

    public ArrayList<Epic> getEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
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
    }

    public Task getTaskById(int id) {
        Task taskToFind = tasks.get(id);
        return taskToFind;
    }

    public SubTask getSubTaskById(int id) {
        SubTask subtaskToFind = subTasks.get(id);
        return subtaskToFind;
    }

    public Epic getEpicById(int id) {
        Epic epicToFind = epics.get(id);
        return epicToFind;
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
        Epic epicWithSubtasks = epics.get(id);
        return epicWithSubtasks.getAssignedSubTasks(epicWithSubtasks);
    }

    public void deleteTaskById(int id) {
        Task taskToDelete = tasks.remove(id);
    }

    public void deleteSubtaskById(int id) {
        SubTask subTaskToDelete = subTasks.remove(id);
        Epic masterEpic = epics.get(subTaskToDelete.getEpicId());
        masterEpic.removeSubTaskFromEpic(subTaskToDelete);
        updateEpicStatus(epics.get(subTaskToDelete.getEpicId()));
    }

    public void deleteEpicById(int id) {
        Epic epicToDelete = epics.remove(id);
        SubTask assignSubTask = subTasks.get(epicToDelete.getId());
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
        int index = masterEpic.getAssignedSubTasks(masterEpic).indexOf(oldSubtask);
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

    public static void updateEpicStatus(Epic epic) {
        if (epic.getAssignedSubTasks(epic).isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        Collection<SubTask> checkStatus = epic.getAssignedSubTasks(epic);
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


