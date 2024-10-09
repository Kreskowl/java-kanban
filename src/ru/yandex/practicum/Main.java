package ru.yandex.practicum;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("First", "task", Status.DONE);
        Task task2 = new Task("Second", "another task", Status.NEW);
        tm.createNewTask(task1);
        tm.createNewTask(task2);
        Epic epic1 = new Epic("Epic", "3 subtasks");
        Epic epic2 = new Epic("Second epic", "0 subtasks");
        tm.createNewEpic(epic1);
        tm.createNewEpic(epic2);
        SubTask subTask1 = new SubTask("subtask", "belongs to epic1", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("second subtask", "belongs to epic1", epic1.getId(),
                Status.IN_PROGRESS);
        SubTask subTask3 = new SubTask("third subtask", "belongs to epic2", epic2.getId(), Status.DONE);
        tm.createNewSubTask(subTask1);
        tm.createNewSubTask(subTask2);
        tm.createNewSubTask(subTask3);

        tm.getSubTaskById(5);
        System.out.println(tm.getHistory());
        tm.getSubTaskById(6);
        System.out.println(tm.getHistory());
        tm.getEpicById(3);
        System.out.println(tm.getHistory());
        tm.getTaskById(1);
        System.out.println(tm.getHistory());
        tm.deleteTaskById(1);
        System.out.println(tm.getHistory());
        tm.getEpicById(4);
        tm.getTaskById(2);
        tm.deleteEpicById(3);
        System.out.println(tm.getHistory());
    }
}

