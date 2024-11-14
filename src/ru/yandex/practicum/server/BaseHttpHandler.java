package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.GsonProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = GsonProvider.GSON;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
        System.out.println("Sending response with status: " + statusCode);
        if (statusCode == 204) {
            exchange.sendResponseHeaders(statusCode, -1);
        }
        exchange.sendResponseHeaders(statusCode, responseText.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseText.getBytes());
        }
    }

    protected boolean hasIdInPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.matches(".+/\\d+$");
    }

    protected Optional<Integer> extractIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");

        if (segments.length >= 3) {
            try {
                return Optional.of(Integer.parseInt(segments[2]));
            } catch (NumberFormatException notANumberError) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}