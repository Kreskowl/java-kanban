package ru.yandex.practicum.task;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    protected TasksTypes type;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TasksTypes.TASK;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TasksTypes getType() {
        return type;
    }

    public void setType(TasksTypes type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + name + "," + status + "," + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
