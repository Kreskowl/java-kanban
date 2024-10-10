package ru.yandex.practicum;

import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        FileBackedTaskManager manager = Managers.getDefaultFileBackedManager();
        Task task = new Task("task 1", "test", Status.IN_PROGRESS);
        Task task2 = new Task("task 2", "test 2", Status.IN_PROGRESS);
        manager.createNewTask(task);
        manager.createNewTask(task2);
        Epic epic = new Epic("epic", "2 subtasks");
        Epic epic2 = new Epic("epic2", "1 subtask");
        manager.createNewEpic(epic);
        manager.createNewEpic(epic2);
        SubTask subTask = new SubTask("subtask", "epic 1", epic.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask 2", "epic 1", epic.getId(), Status.IN_PROGRESS);
        SubTask subTask3 = new SubTask("subtask 3", "epic 2", epic2.getId(), Status.DONE);
        manager.createNewSubTask(subTask);
        manager.createNewSubTask(subTask2);
        manager.createNewSubTask(subTask3);
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubTasksList());
        System.out.println(manager.getTasksList());
        FileBackedTaskManager testLoad = FileBackedTaskManager.loadFromFile(new File("src/ru/yandex/practicum/resources/savedTasks.txt"));
        System.out.println(testLoad.getEpicsList());
        System.out.println(testLoad.getSubTasksList());
        System.out.println(testLoad.getTasksList());
    }
}

