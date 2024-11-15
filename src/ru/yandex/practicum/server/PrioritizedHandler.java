package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        super();
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equals(method)) {
            handleGet(exchange);
        } else {
            HttpCodeResponse.NOT_ALLOWED.sendResponse(exchange, "Method Not Allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
    }
}

