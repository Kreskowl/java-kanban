package server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.server.HttpCodeResponse;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryHandlerTest extends HttpTaskServerTestBase{
    private Task task;
    private Epic epic;
    private static final Logger logger = Logger.getLogger(HistoryHandlerTest.class.getName());

    public HistoryHandlerTest() throws IOException {
        super();
    }


    @DisplayName("Should return history list")
    @Test
    public void shouldReturnHistoryList() {
        task = new Task("task", "task", Status.NEW, LocalDateTime.now(), 30);
        epic = new Epic("epic", "epic");
        manager.createNewTask(task);
        manager.createNewEpic(epic);
        manager.getEpicById(epic.getId());
        manager.getTaskById(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.OK.getCode(), response.statusCode(), "code should be 200");

        Type historyList = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), historyList);

        assertEquals(manager.getHistory().size(), history.size(), "size should be equal");
        assertEquals(manager.getHistory().get(0), history.get(0), "objects should be equal");
        assertEquals(manager.getHistory().get(1), history.get(1), "objects should be equal");
    }

    @DisplayName("Should return 405 for any method except GET")
    @Test
    public void shouldReturnCorrectResponseCodeIfRequestIsNotGet() {
        task = new Task("task", "task", Status.NEW, LocalDateTime.now(), 30);
        manager.createNewTask(task);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.NOT_ALLOWED.getCode(), response.statusCode(), "response code should be 405");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.NOT_ALLOWED.getCode(), response.statusCode(), "response code should be 405");
    }
}
