package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.adapters.GsonProvider;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final int BACKLOG = 0;
    private final HttpServer server;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), BACKLOG);
        this.gson = GsonProvider.GSON;
        setupEndpoints();
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
            server.start();
        } catch (IOException serverCreateError) {
            System.err.println("Failed to start server: " + serverCreateError.getMessage());
            serverCreateError.printStackTrace();
        }
    }

    public Gson getGson() {
        return gson;
    }

    private void setupEndpoints() {
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/subtasks", new SubTasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(BACKLOG);
    }
}
