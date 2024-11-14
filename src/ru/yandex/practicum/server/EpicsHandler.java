package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends TasksHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Optional<Integer> idOpt = extractIdFromPath(exchange);

        if (idOpt.isPresent()) {
            Optional<Epic> epic = taskManager.getEpicById(idOpt.get());

            if (path.endsWith("/subtasks")) {
                List<SubTask> subTasks = taskManager.getAssignedSubTasks(idOpt.get());
                sendResponse(exchange, 200, gson.toJson(subTasks));
            } else {
                sendResponse(exchange, 200, gson.toJson(epic.get()));
            }
        } else {
            sendResponse(exchange, 200, gson.toJson(taskManager.getEpicsList()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Epic.class);
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.updateEpic(epic);
            sendResponse(exchange, 201, "Epic updated successfully");
        } else {
            taskManager.createNewEpic(epic);
            sendResponse(exchange, 201, "Epic created successfully");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.deleteEpicById(idOpt.get());
            sendResponse(exchange, 204, "Epic deleted");
        } else {
            taskManager.clearEpics();
            sendResponse(exchange, 204, "All epics deleted");
        }
    }
}
