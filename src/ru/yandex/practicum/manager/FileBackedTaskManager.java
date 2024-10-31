package ru.yandex.practicum.manager;

import ru.yandex.practicum.Util.DateAndTimeFormatUtil;
import ru.yandex.practicum.Util.Managers;
import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private boolean isLoading = false;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
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

    @Override
    public void clearTasks() {
        super.clearTasks();
        autoSave();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        autoSave();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        autoSave();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        autoSave();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        autoSave();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        autoSave();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        autoSave();
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        super.updateSubTask(updatedSubTask);
        autoSave();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        autoSave();
    }

    private void autoSave() {
        try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write("id, type, name, status, description, epicId, start time, duration\n");

            for (Task task : getEpicsList()) {
                writer.write(toString(task) + "\n");
            }
            for (Task task : getTasksList()) {
                writer.write(toString(task) + "\n");
            }
            for (Task task : getSubTasksList()) {
                writer.write(toString(task) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error saving data in file: " + file.getPath());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        manager.isLoading = true;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            int actualId = 0;
            while ((line = reader.readLine()) != null) {
                Task task = taskFromString(line);

                switch (task.getType()) {
                    case EPIC -> manager.getEpics().put(task.getId(), (Epic) task);
                    case SUBTASK -> {
                        SubTask subTask = (SubTask) task;
                        manager.getSubTasks().put(task.getId(), (SubTask) task);
                        manager.getEpics().get(subTask.getEpicId()).addSubTask(subTask);
                    }
                    default -> manager.getTasks().put(task.getId(), task);
                }

                if (task.getId() > actualId) {
                    actualId = task.getId();
                }
            }
            manager.getEpics().values().forEach(manager::updateEpicStatus);
            manager.setCurrentId(actualId + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Error loading from file: " + file.getAbsolutePath());
        } finally {
            manager.isLoading = false;
        }
        return manager;
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId() + ","
                + DateAndTimeFormatUtil.formatDateTime(task.getStartTime()) + ","
                + DateAndTimeFormatUtil.formatDurationTime(task.getDuration());
    }

    private static Task taskFromString(String value) {
        String[] info = value.split(",");
        int id = Integer.parseInt(info[0]);
        TasksTypes type = TasksTypes.valueOf(info[1].toUpperCase());
        String name = info[2];
        Status status = Status.valueOf(info[3].toUpperCase().replace(" ", "_"));
        String description = info[4];
        LocalDateTime startTime = DateAndTimeFormatUtil.parseDateTime(info[6]);
        Duration duration = DateAndTimeFormatUtil.parseFormattedDuration(info[7]);

        switch (type) {
            case EPIC -> {
                return new Epic(name, description, id);
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(info[5]);
                return new SubTask(name, description, id, status, epicId, startTime, duration.toMinutes());
            }
            default -> {
                return new Task(name, description, id, status, startTime, duration.toMinutes());
            }
        }
    }

    private void initialize(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new ManagerSaveException("Error creating file: " + e.getMessage());
        }
    }
}
