package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum HttpCodeResponse {
    OK(200),
    CREATED(201),
    DELETED(204),
    NOT_FOUND(404),
    NOT_ALLOWED(405),
    OVERLAPS(406),
    INTERNAL_SERVER_ERROR(500);

    private final int code;
    private static final Logger logger = Logger.getLogger(HttpCodeResponse.class.getName());

    HttpCodeResponse(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void sendResponse(HttpExchange exchange, String responseText) {
        try {
            if (code == 204) {
                exchange.sendResponseHeaders(code, -1);
            } else {
                byte[] responseBytes = responseText.getBytes();
                exchange.sendResponseHeaders(code, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            }
        } catch (IOException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response with code: " + code, serverResponseError);
        }

    }
}
