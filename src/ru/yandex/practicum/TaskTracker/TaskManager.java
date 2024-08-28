package ru.yandex.practicum.TaskTracker;

import java.util.*;

public class TaskManager {
    private static HashMap<Integer, Task> tasks = new HashMap<>();
    private static HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private static HashMap<Integer, Epic> epics = new HashMap<>();
    private int currentId = 1;

    public Collection<Task> getTasksList() {
        return tasks.values();
    }

    public Collection<SubTask> getSubTasksList() {
        return subTasks.values();
    }

    public Collection<Epic> getEpicsList() {
        return epics.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();
        Epic.clearAssignedSubTasks();
        for (Epic epic : epics.values()) {
            Epic.updateEpicStatus(epic);
        }
    }

    public void clearEpics() {
        epics.clear();
        Epic.clearAssignedSubTasks();
        clearSubTasks();
    }

    public Task getTaskById(int id) {
        Task taskToFind = tasks.get(id);
        if (taskToFind != null) {
            return taskToFind;
        } else {
            return null;
        }
    }

    public SubTask getSubTaskById(int id) {
        SubTask subtaskToFind = subTasks.get(id);
        if (subtaskToFind != null) {
            return subtaskToFind;
        } else {
            return null;
        }
    }

    public Epic getEpicById(int id) {
        Epic epicToFind = epics.get(id);
        if (epicToFind != null) {
            return epicToFind;
        } else {
            return null;
        }
    }

    public void createNewTask(Task task) {
        task.setId(currentId);
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        newTask.setId(currentId);
        tasks.put(currentId, newTask);
        currentId++;
    }

    public void createNewSubTask(SubTask subTask) {
        subTask.setId(currentId);
        SubTask newSubTask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getEpicId(),
                subTask.getStatus());
        newSubTask.setId(currentId);
        Epic epic = epics.get(newSubTask.getEpicId());
        if (epic != null) {
            Epic.addSubTask(newSubTask);
            Epic.updateEpicStatus(epics.get(newSubTask.getEpicId()));
            subTasks.put(currentId, newSubTask);
            currentId++;
        } else {
            return;
        }
    }

    public void createNewEpic(Epic epic) {
        epic.setId(currentId);
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(currentId);
        epics.put(currentId, newEpic);
        Epic.updateEpicStatus(newEpic);
        currentId++;
    }

    public Collection<SubTask> getAssignedSubTasks(Epic epic) {
        int id = epic.getId();
        Epic epicWithSubtasks = epics.get(id);
        return Epic.getAssignedSubTasks(epicWithSubtasks);
    }

    public void deleteTaskById(int id) {
        Task taskToDelete = tasks.get(id);
        if (taskToDelete != null) {
            tasks.remove(id);
        } else {
            return;
        }
    }

    public void deleteSubtaskById(int id) {
        SubTask subTaskToDelete = subTasks.get(id);
        Epic masterEpic = epics.get(subTaskToDelete.getEpicId());
        if (subTaskToDelete != null) {
            subTasks.remove(id);
            Collection<SubTask> assignedSubTasks = Epic.getAssignedSubTasks(masterEpic);
            assignedSubTasks.remove(subTaskToDelete);
            Epic.updateEpicStatus(epics.get(subTaskToDelete.getEpicId()));
        } else {
            return;
        }
    }

    public void deleteEpicById(int id) {
        Epic epicToDelete = epics.get(id);
        if (epicToDelete != null) {
            subTasks.keySet().removeIf(subTaskId -> subTasks.get(subTaskId).getEpicId() == id);
            Epic.removeAssignedTasks(epics.get(id));
            epics.remove(id);
        } else {
            return;
        }
    }

    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();
        tasks.put(id, updatedTask);
    }

    public void updateSubTask(SubTask updatedSubTask) {
        int id = updatedSubTask.getId();
        SubTask oldSubTask = subTasks.get(id);
        Epic masterEpic = epics.get(updatedSubTask.getEpicId());

        Collection<SubTask> subTasksCollection = Epic.getAssignedSubTasks(masterEpic);
        List<SubTask> subTaskList = (List<SubTask>) subTasksCollection;
        int index = subTaskList.indexOf(oldSubTask);

        subTaskList.set(index, updatedSubTask);
        subTasks.put(id, updatedSubTask);
        Epic.updateEpicStatus(epics.get(updatedSubTask.getEpicId()));
    }

    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        epics.put(id, updatedEpic);
        Epic.updateEpicStatus(updatedEpic);
    }
}


