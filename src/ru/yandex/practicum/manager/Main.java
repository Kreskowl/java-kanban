package ru.yandex.practicum.manager;

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
        Epic epic1 = new Epic("Epic", "2 subtasks", Status.NEW);
        Epic epic2 = new Epic("Second epic", "1 subtask", Status.DONE);
        tm.createNewEpic(epic1);
        tm.createNewEpic(epic2);
        SubTask subTask1 = new SubTask("subtask", "belongs to epic1", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("second subtask", "belongs to epic1", epic1.getId(),
                Status.IN_PROGRESS);
        SubTask subTask3 = new SubTask("third subtask", "belongs to epic2", epic2.getId(), Status.DONE);
        tm.createNewSubTask(subTask1);
        tm.createNewSubTask(subTask2);
        tm.createNewSubTask(subTask3);

        tm.clearTasks();
        System.out.println(tm.getTasksList());

        System.out.println(tm.getSubTaskById(6));
        System.out.println(tm.getSubTaskById(6));
        System.out.println(tm.getSubTaskById(6));
        System.out.println(tm.getSubTaskById(6));
        System.out.println(tm.getSubTaskById(6));
        System.out.println(tm.getSubTaskById(6));
        System.out.println(tm.getHistory());
        System.out.println(tm.getEpicById(3));
        System.out.println(tm.getEpicById(3));
        System.out.println(tm.getTaskById(1));
        System.out.println(tm.getTaskById(2));
        System.out.println(tm.getHistory());
        System.out.println(tm.getEpicById(3));
        System.out.println(tm.getHistory());


    }
}
