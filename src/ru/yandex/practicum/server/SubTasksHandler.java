package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubTasksHandler extends TasksHandler {
    public SubTasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);

        if (hasIdInPath(exchange) && idOpt.isPresent()) {
            Optional<SubTask> subTask = taskManager.getSubTaskById(idOpt.get());
            sendResponse(exchange, 200, gson.toJson(subTask.get()));
        } else {
            sendResponse(exchange, 200, gson.toJson(taskManager.getSubTasksList()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        SubTask subTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), SubTask.class);
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.updateSubTask(subTask);
            sendResponse(exchange, 201, "Subtask updated successfully");
        } else {
            try {
                taskManager.createNewSubTask(subTask);
                sendResponse(exchange, 201, "Subtask created successfully");
            } catch (IllegalArgumentException timeOverlap) {
                sendResponse(exchange, 406, "Subtask time overlaps with an existing task or subtask");
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.deleteSubtaskById(idOpt.get());
            sendResponse(exchange, 204, "Subtask deleted");
        } else {
            taskManager.clearSubTasks();
            sendResponse(exchange, 204, "All subtasks deleted");
        }
    }
}
