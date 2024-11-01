package ru.yandex.practicum;

import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.util.Managers;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("task 1", "test", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 10, 29, 10, 0), 30);
//       Task task2 = new Task("task 2", "test 2", Status.IN_PROGRESS);
        manager.createNewTask(task);
//        manager.createNewTask(task2);
        Epic epic = new Epic("epic", "2 subtasks");
//        Epic epic2 = new Epic("epic2", "1 subtask");
        manager.createNewEpic(epic);
//        manager.createNewEpic(epic2);
        SubTask subTask = new SubTask("subtask", "epic 1", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        SubTask subTask2 = new SubTask("subtask 2", "epic 1", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 30, 19, 0), 30);
//        SubTask subTask3 = new SubTask("subtask 3", "epic 2", epic2.getId(), Status.DONE);
        manager.createNewSubTask(subTask);
        System.out.println(epic.getStartTime());
        System.out.println(epic.getDuration());
        System.out.println(epic.getEndTime());
        manager.createNewSubTask(subTask2);
//        manager.createNewSubTask(subTask3);
        System.out.println(epic.getStartTime());
        System.out.println(epic.getDuration());
        System.out.println(epic.getEndTime());
//        FileBackedTaskManager testLoad = FileBackedTaskManager.loadFromFile(new File("src/ru/yandex/practicum/resources/savedTasks.txt"));
//        System.out.println(testLoad.getEpicsList());
//        System.out.println(testLoad.getSubTasksList());
//        System.out.println(testLoad.getTasksList());
    }
}

