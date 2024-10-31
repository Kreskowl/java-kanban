package ru.yandex.practicum.manager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getTasksList();

    List<SubTask> getSubTasksList();

    List<Epic> getEpicsList();

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Optional<Task> getTaskById(int id);

    Optional<SubTask> getSubTaskById(int id);

    Optional<Epic> getEpicById(int id);

    void createNewTask(Task task);

    void createNewSubTask(SubTask subTask);

    void createNewEpic(Epic epic);

    List<SubTask> getAssignedSubTasks(int id);

    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    void updateTask(Task updatedTask);

    void updateSubTask(SubTask updatedSubTask);

    void updateEpic(Epic updatedEpic);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}


