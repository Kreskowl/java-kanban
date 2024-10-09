package ru.yandex.practicum.manager;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager history = Managers.getDefaultHistory();

    private int currentId = 1;

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearAssignedSubTasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        history.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        history.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        history.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void createNewTask(Task task) {
        task.setId(currentId);
        tasks.put(currentId, task);
        currentId++;
    }

    @Override
    public void createNewSubTask(SubTask subTask) {
        subTask.setId(currentId);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTask(subTask);
            updateEpicStatus(epics.get(subTask.getEpicId()));
            subTasks.put(currentId, subTask);
            currentId++;
        }
    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setId(currentId);
        epics.put(currentId, epic);
        currentId++;
    }

    @Override
    public List<SubTask> getAssignedSubTasks(int id) {
        return epics.get(id).getAssignedSubTasks();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        SubTask subTaskToDelete = subTasks.remove(id);
        Epic masterEpic = epics.get(subTaskToDelete.getEpicId());
        masterEpic.removeSubTaskFromEpic(subTaskToDelete);
        updateEpicStatus(epics.get(subTaskToDelete.getEpicId()));
        history.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<SubTask> assignedSubtasks = getAssignedSubTasks(id);
        epics.remove(id);
        history.remove(id);
        for (SubTask subtask : assignedSubtasks) {
            history.remove(subtask.getId());
        }
        subTasks.keySet().removeIf(subTaskId -> subTasks.get(subTaskId).getEpicId() == id);
    }

    @Override
    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();
        tasks.put(id, updatedTask);
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        int id = updatedSubTask.getId();
        Epic masterEpic = epics.get(updatedSubTask.getEpicId());

        SubTask oldSubtask = subTasks.get(id);
        masterEpic.removeSubTaskFromEpic(oldSubtask);
        masterEpic.addSubTask(updatedSubTask);
        subTasks.put(id, updatedSubTask);
        updateEpicStatus(epics.get(updatedSubTask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        Epic oldEpic = epics.get(id);
        List<SubTask> existingSubTasks = oldEpic.getAssignedSubTasks();

        for (SubTask subTask : existingSubTasks) {
            updatedEpic.addSubTask(subTask);
        }
        epics.put(id, updatedEpic);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<SubTask> checkStatus = epic.getAssignedSubTasks();

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


