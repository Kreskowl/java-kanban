package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.NotFoundException;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    protected final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET" -> handleGet(exchange);
                case "POST" -> handlePost(exchange);
                case "DELETE" -> handleDelete(exchange);
                default -> HttpCodeResponse.NOT_ALLOWED.sendResponse(exchange, "Method Not Allowed");
            }
        } catch (NotFoundException notFound) {
            HttpCodeResponse.NOT_FOUND.sendResponse(exchange, notFound.getMessage());
        }
    }

    protected void handleGet(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        if (idOpt.isPresent()) {
            Task task = taskManager.getTaskById(idOpt.get())
                    .orElseThrow(() -> new NotFoundException("Task with id " + idOpt.get() + " not found"));
            HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(task));
        } else {
            HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(taskManager.getTasksList()));
        }
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        Task task = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Task.class);
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.updateTask(task);
            HttpCodeResponse.CREATED.sendResponse(exchange, "Task updated successfully");
        } else {
            try {
                taskManager.createNewTask(task);
                HttpCodeResponse.CREATED.sendResponse(exchange, "Task created successfully");
            } catch (IllegalArgumentException timeOverlap) {
                HttpCodeResponse.OVERLAPS.sendResponse(exchange, "Task time overlaps with an existing task or subtask");
            }
        }
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.deleteTaskById(idOpt.get());
            HttpCodeResponse.DELETED.sendResponse(exchange, "Task deleted");
        } else {
            taskManager.clearTasks();
            HttpCodeResponse.DELETED.sendResponse(exchange, "All tasks deleted");
        }
    }
}
