package ru.yandex.practicum.manager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("First", "task", Status.DONE);
        Task task2 = new Task("Second", "another task", Status.NEW);
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);
        Epic epic1 = new Epic("Epic", "2 subtasks", Status.NEW);
        Epic epic2 = new Epic("Second epic", "1 subtask", Status.DONE);
        taskManager.createNewEpic(epic1);
        taskManager.createNewEpic(epic2);
        SubTask subTask1 = new SubTask("subtask", "belongs to epic1", epic1.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("second subtask", "belongs to epic1", epic1.getId(),
                Status.IN_PROGRESS);
        SubTask subTask3 = new SubTask("third subtask", "belongs to epic2", epic2.getId(), Status.DONE);
        taskManager.createNewSubTask(subTask1);
        taskManager.createNewSubTask(subTask2);
        taskManager.createNewSubTask(subTask3);
        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getSubTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getAssignedSubTasks(3));
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(4);
        taskManager.deleteSubtaskById(5);

        Task updatedTask2 = new Task("Update second", "update success", Status.IN_PROGRESS);
        updatedTask2.setId(2);
        taskManager.updateTask(updatedTask2);
        SubTask updateSubTask2 = new SubTask("Update subtask", "update success",
                epic1.getId(), Status.NEW);
        updateSubTask2.setId(6);
        taskManager.updateSubTask(updateSubTask2);
        Epic updateEpic = new Epic("update epic", "update success", Status.IN_PROGRESS);
        updateEpic.setId(3);
        taskManager.updateEpic(updateEpic);

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubTaskById(6));
        System.out.println(taskManager.getAssignedSubTasks(3));
    }
}
