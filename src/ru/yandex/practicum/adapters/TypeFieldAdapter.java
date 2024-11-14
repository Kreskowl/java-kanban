package ru.yandex.practicum.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TasksTypes;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class TypeFieldAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter out, Task task) throws IOException {
        out.beginObject();
        out.name("name").value(task.getName());
        out.name("description").value(task.getDescription());
        out.name("id").value(task.getId());
        out.name("status").value(task.getStatus().name());
        out.name("type").value(task.getType().name());  // Используемое для бизнес-логики
        out.name("taskClassType").value(task.getClass().getSimpleName().toUpperCase()); // Добавляем маркерное поле

        if (task.getDuration() != null) {
            out.name("duration").value(task.getDuration().toMinutes());
        }

        if (task.getStartTime() != null) {
            out.name("startTime").value(task.getStartTime().toString());
        }

        if (task.getEndTime() != null) {
            out.name("endTime").value(task.getEndTime().toString());
        }

        out.endObject();
    }

    @Override
    public Task read(JsonReader in) throws IOException {
        in.beginObject();

        String name = null;
        String description = null;
        int id = 0;
        Status status = null;
        TasksTypes type = null;
        Duration duration = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "name":
                    name = in.nextString();
                    break;
                case "description":
                    description = in.nextString();
                    break;
                case "id":
                    id = in.nextInt();
                    break;
                case "status":
                    status = Status.valueOf(in.nextString());
                    break;
                case "type":
                    type = TasksTypes.valueOf(in.nextString()); // Используется для бизнес-логики
                    break;
                case "duration":
                    duration = Duration.ofMinutes(in.nextLong());
                    break;
                case "startTime":
                    startTime = LocalDateTime.parse(in.nextString());
                    break;
                case "endTime":
                    endTime = LocalDateTime.parse(in.nextString());
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();

        Task task;
        if (type == TasksTypes.EPIC) {
            task = new Epic(name, description);
        } else if (type == TasksTypes.SUBTASK) {
            task = new SubTask(name, description, status, id, startTime, (int) duration.toMinutes());
        } else {
            task = new Task(name, description, status, startTime, (int) duration.toMinutes());
        }

        task.setId(id);
        task.setStatus(status);
        task.setDuration(duration);
        task.setStartTime(startTime);
        task.setEndTime(endTime);

        return task;
    }
}

