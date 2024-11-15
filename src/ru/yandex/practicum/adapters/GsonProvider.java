package ru.yandex.practicum.adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class GsonProvider {
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new DateTimeAdapter())
            .registerTypeAdapter(Task.class, new TypeFieldAdapter())
            .create();
}

