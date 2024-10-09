package ru.yandex.practicum.task;

public enum TasksTypes {
    TASK,
    SUBTASK,
    EPIC;

    @Override
    public String toString() {
        String name = name().toLowerCase();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}


