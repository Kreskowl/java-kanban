package ru.yandex.practicum.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected TasksTypes type;
    private String name;
    private final String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = TasksTypes.TASK;
        this.status = Status.NEW;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ZERO;
        this.endTime = calculateEndTime(this.startTime, this.duration);
    }


    public Task(String name, String description, int id) {
        this(name, description);
        this.id = id;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, long durationInMinutes) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TasksTypes.TASK;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(durationInMinutes);
        this.endTime = calculateEndTime(this.startTime, this.duration);
    }

    public Task(String name, String description, int id, Status status, LocalDateTime startTime, long durationInMinutes) {
        this(name, description, status, startTime, durationInMinutes);
        this.id = id;
    }

    public Integer getEpicId() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TasksTypes getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        return startTime != null ? startTime.plusMinutes(duration.toMinutes()) : null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
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

    @Override
    public int compareTo(Task other) {
        if (this.startTime == null && other.startTime == null) {
            return 0;
        } else if (this.startTime == null) {
            return 1;
        } else if (other.startTime == null) {
            return -1;
        }
        return this.startTime.compareTo(other.startTime);
    }
}




