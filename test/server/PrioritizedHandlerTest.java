package server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest extends HttpTaskServerTestBase{
    private Task firstByTime;
    private Task lastByTime;
    private final LocalDateTime firstTime = LocalDateTime.of(2024, 10, 12,8,8,0);
    private final LocalDateTime lastTime = LocalDateTime.of(2024, 11, 14,10,10,0);

    public PrioritizedHandlerTest() throws IOException {
        super();
    }

    @DisplayName("Should return list of prioritized objects by time")
    @Test
    public void shouldReturnListOfPrioritizedObjectsByTime() {
        lastByTime = new Task("Task 1", "should be last", Status.NEW, lastTime, 30);
        firstByTime = new Task("Task 2", "should be first", Status.NEW, firstTime, 30);
        manager.createNewTask(lastByTime);
        manager.createNewTask(firstByTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.printStackTrace();
        }

        assertEquals(200, response.statusCode(), "response code should be 200");

        Type setOfPrioritizedType = new TypeToken<LinkedHashSet<Task>>() {}.getType();
        LinkedHashSet<Task> serverPrioritized = gson.fromJson(response.body(), setOfPrioritizedType);

        assertEquals(manager.getPrioritizedTasks().size(), serverPrioritized.size(), "Size is not equal");

        List<Task> prioritizedList = List.copyOf(serverPrioritized);
        assertEquals(firstByTime, prioritizedList.get(0), "Task should be first in set");
        assertEquals(lastByTime, prioritizedList.get(prioritizedList.size() - 1), "Task should be last in set");
    }

    @DisplayName("Should return 405 for any method except GET")
    @Test
    public void shouldReturnCorrectResponseCodeIfRequestIsNotGet() {
        firstByTime = new Task("Task 2", "should be first", Status.NEW, firstTime, 30);
        manager.createNewTask(firstByTime);
        String taskJson = gson.toJson(firstByTime);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.printStackTrace();
        }

        assertEquals(405, response.statusCode(), "response code should be 405");

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.printStackTrace();
        }

        assertEquals(405, response.statusCode(), "response code should be 405");
    }
}
