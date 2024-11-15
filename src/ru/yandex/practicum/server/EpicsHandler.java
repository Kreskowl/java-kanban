package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.NotFoundException;
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
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        Optional<Integer> idOpt = extractIdFromPath(exchange);

        if (idOpt.isPresent()) {
            Epic epic = taskManager.getEpicById(idOpt.get())
                    .orElseThrow(() -> new NotFoundException("Epic with id " + idOpt.get() + " not found"));

            if (path.endsWith("/subtasks")) {
                List<SubTask> subTasks = taskManager.getAssignedSubTasks(epic.getId());
                HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(subTasks));
            } else {
                HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(epic));
            }
        } else {
            HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(taskManager.getEpicsList()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        Epic epic = gson.fromJson(new String(exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8), Epic.class);
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.updateEpic(epic);
            HttpCodeResponse.CREATED.sendResponse(exchange, "Epic updated successfully");
        } else {
            taskManager.createNewEpic(epic);
            HttpCodeResponse.CREATED.sendResponse(exchange, "Epic created successfully");
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> idOpt = extractIdFromPath(exchange);
        if (idOpt.isPresent()) {
            taskManager.deleteEpicById(idOpt.get());
            HttpCodeResponse.DELETED.sendResponse(exchange, "Epic deleted");
        } else {
            taskManager.clearEpics();
            HttpCodeResponse.DELETED.sendResponse(exchange, "All epics deleted");
        }
    }
}
