package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;


    public HistoryHandler(TaskManager taskManager) {
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
        String jsonResponse = gson.toJson(taskManager.getHistory());
        System.out.println("Serialized history list response: " + jsonResponse);
        HttpCodeResponse.OK.sendResponse(exchange, gson.toJson(taskManager.getHistory()));
    }
}

