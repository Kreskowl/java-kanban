package ru.yandex.practicum.manager;

import ru.yandex.practicum.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private boolean isLoading = false;

    public FileBackedTaskManager(File file) {
        this.file = file;
        if (!file.exists()) {
            initialize(file);
        }
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        if (!isLoading) {
            autoSave();
        }
    }

    @Override
    public void createNewSubTask(SubTask subTask) {
        super.createNewSubTask(subTask);
        if (!isLoading) {
            autoSave();
        }
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        if (!isLoading) {
            autoSave();
        }
    }

    private void autoSave() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id, type, name, status, description, epicId\n");

            for (Task task : getEpicsList()) {
                writer.write(task.toString() + "\n");
            }
            for (Task task : getTasksList()) {
                writer.write(task.toString() + "\n");
            }
            for (Task task : getSubTasksList()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data in file: " + file.getPath());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.isLoading = true;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            int actualId = 0;
            while ((line = reader.readLine()) != null) {
                Task task = taskFromString(line);

                switch (task.getType()) {
                    case EPIC -> manager.getEpics().put(task.getId(), (Epic) task);
                    case SUBTASK -> manager.getSubTasks().put(task.getId(), (SubTask) task);
                    default -> manager.getTasks().put(task.getId(), task);
                }

                if (task.getId() > actualId) {
                    actualId = task.getId();
                }
            }
            manager.setCurrentId(actualId + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading from file: " + file.getPath());
        } finally {
            manager.isLoading = false;
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
}
