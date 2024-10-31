package ru.yandex.practicum.task;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, Status status, int epicId,
                   LocalDateTime startTime, long duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
        this.type = TasksTypes.SUBTASK;
    }

    public SubTask(String name, String description, int id, Status status, int epicId,
                   LocalDateTime startTime, long durationInMinutes) {
        super(name, description, id, status, startTime, durationInMinutes);
        this.epicId = epicId;
        this.type = TasksTypes.SUBTASK;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }
}

