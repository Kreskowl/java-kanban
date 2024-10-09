package ru.yandex.practicum.manager;

import ru.yandex.practicum.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private final List<Task> createdTasks = new ArrayList<>();

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            initialize(file);
        }
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        createdTasks.add(task);
        autoSave();
    }

    @Override
    public void createNewSubTask(SubTask subTask) {
        super.createNewSubTask(subTask);
        createdTasks.add(subTask);
        autoSave();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        createdTasks.add(epic);
        autoSave();
    }

    private void autoSave() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id, type, name, status, description, epicId\n");

            for (Task task : createdTasks) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data in file: " + file.getPath());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = taskFromString(line);

                switch (task.getType()) {
                    case EPIC -> manager.createNewEpic((Epic) task);
                    case SUBTASK -> manager.createNewSubTask((SubTask) task);
                    default -> manager.createNewTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading from file: " + file.getPath());
        }
        return manager;
    }

    private static Task taskFromString(String value) {
        String[] info = value.split(",");
        int id = Integer.parseInt(info[0]);
        TasksTypes type = TasksTypes.valueOf(info[1].toUpperCase());
        String name = info[2];
        Status status = Status.valueOf(info[3].toUpperCase().replace(" ", "_"));
        String description = info[4];

        switch (type) {
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(info[5]);
                SubTask subtask = new SubTask(name, description, epicId, status);
                subtask.setId(id);
                return subtask;
            }
            default -> {
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            }
        }
    }

    private void initialize(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            //есть предупреждение от идеа об этих вызовах, решил что правильно будет игнорировать предупреждения,
            // т.к неважно создана директория с файлом или нет, главное чтобы файл был
        } catch (IOException e) {
            throw new ManagerSaveException("Error creating file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        FileBackedTaskManager manager = Managers.getDefaultFileBackedManager();
        Task task = new Task("task 1", "test", Status.IN_PROGRESS);
        Task task2 = new Task("task 2", "test 2", Status.IN_PROGRESS);
        Epic epic = new Epic("epic", "2 subtasks");
        Epic epic2 = new Epic("epic2", "1 subtask");
        manager.createNewEpic(epic);
        manager.createNewEpic(epic2);
        manager.createNewTask(task);
        manager.createNewTask(task2);
        SubTask subTask = new SubTask("subtask", "epic 1", epic.getId(), Status.NEW);
        SubTask subTask2 = new SubTask("subtask 2", "epic 1", epic.getId(), Status.IN_PROGRESS);
        SubTask subTask3 = new SubTask("subtask 3", "epic 2", epic2.getId(), Status.DONE);
        manager.createNewSubTask(subTask);
        manager.createNewSubTask(subTask2);
        manager.createNewSubTask(subTask3);
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubTasksList());
        System.out.println(manager.getTasksList());
        FileBackedTaskManager testLoad = loadFromFile(new File
                ("src/ru/yandex/practicum/resources/savedTasks.txt"));
        System.out.println(testLoad.getEpicsList());
        System.out.println(testLoad.getSubTasksList());
        System.out.println(testLoad.getTasksList());
        testLoad.createNewTask(task);
    }
}
