package ru.yandex.practicum.manager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasksList();

    List<SubTask> getSubTasksList();

    List<Epic> getEpicsList();

    void clearTasks();

    void clearSubTasks();

    void clearEpics();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

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

}


