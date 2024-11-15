package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.NotFoundException;
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
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        if (hasIdInPath(exchange) && idOpt.isPresent()) {
            SubTask subTask = taskManager.getSubTaskById(idOpt.get())
                    .orElseThrow(() -> new NotFoundException("Subtask with id " + idOpt.get() + " not found"));
            HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(subTask));
        } else {
            HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(taskManager.getSubTasksList()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        SubTask subTask = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), SubTask.class);
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.updateSubTask(subTask);
            HttpCodeResponse.CREATED.sendResponse(exchange, "Subtask updated successfully");
        } else {
            try {
                taskManager.createNewSubTask(subTask);
                HttpCodeResponse.CREATED.sendResponse(exchange, "Subtask created successfully");
            } catch (IllegalArgumentException timeOverlap) {
                HttpCodeResponse.OVERLAPS.sendResponse(exchange, "Subtask time overlaps with an existing task or subtask");
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.deleteSubtaskById(idOpt.get());
            HttpCodeResponse.DELETED.sendResponse(exchange, "Subtask deleted");
        } else {
            taskManager.clearSubTasks();
            HttpCodeResponse.DELETED.sendResponse(exchange, "All subtasks deleted");
        }
    }
}
