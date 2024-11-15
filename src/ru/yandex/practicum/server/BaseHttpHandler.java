package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.adapters.GsonProvider;

import java.io.IOException;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {
    protected final Gson gson;

    public BaseHttpHandler() {
        this.gson = GsonProvider.GSON;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
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