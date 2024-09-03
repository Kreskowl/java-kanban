package ru.yandex.practicum.task;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int epicId, Status status) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}

