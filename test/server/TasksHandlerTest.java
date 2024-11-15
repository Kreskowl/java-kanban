package server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.server.HttpCodeResponse;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TasksHandlerTest extends HttpTaskServerTestBase {

    private Task task;
    private final LocalDateTime fixedTime = LocalDateTime.of(2024, 10, 9, 10, 0);
    private static final Logger logger = Logger.getLogger(TasksHandlerTest.class.getName());

    public TasksHandlerTest() throws IOException, InterruptedException {
        super();
    }

    @DisplayName("Should add task with POST request without id")
    @Test
    public void shouldAddTaskWithPostRequestWithoutId() {
        task = new Task("Test 2", "Testing task 2", Status.NEW, fixedTime, 5);
        String taskJson = gson.toJson(task);
        task.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }
        assertEquals(HttpCodeResponse.CREATED.getCode(), response.statusCode());

        assertEquals(1, manager.getTasksList().size(), "Task is not added in list");
        assertEquals(task, manager.getTasksList().get(0), "Task in list is not equal with posted");
    }

    @DisplayName("Should update task with POST request with id")
    @Test
    public void shouldUpdateTaskWithPostRequestWithId() {
        task = new Task("Test 2", "Testing task 2", Status.NEW, fixedTime, 5);
        manager.createNewTask(task);
        task.setStatus(Status.IN_PROGRESS);
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.CREATED.getCode(), response.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = null;
        try {
            getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        Task updatedTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(task, updatedTask, "Task was not updated");
    }

    @DisplayName("Should return tasks list with GET request without id")
    @Test
    public void shouldReturnTasksListWithGetRequestWithoutId() {
        task = new Task("Test 2", "Testing task 2", Status.NEW, fixedTime, 5);
        manager.createNewTask(task);
        List<Task> tasks = manager.getTasksList();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
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

        Type listOfTaskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> serverTasks = gson.fromJson(response.body(), listOfTaskType);

        assertEquals(HttpCodeResponse.OK.getCode(), response.statusCode());
        assertEquals(manager.getTasksList().size(), serverTasks.size(), "Size of the list is not correct");
        assertEquals(manager.getTasksList(), serverTasks, "List not matched");
    }

    @DisplayName("Should return task with GET request with id")
    @Test
    public void shouldReturnTaskWithGetRequestWithId() {
        task = new Task("Test 2", "Testing task 2", Status.NEW, fixedTime, 5);
        manager.createNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.OK.getCode(), response.statusCode());

        Task requestedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task, requestedTask, "Get wrong task");
    }

    @DisplayName("Should delete task with DELETE request")
    @Test
    public void shouldDeleteTaskWithDeleteRequest() {
        task = new Task("Test 2", "Testing task 2", Status.NEW, fixedTime, 5);
        manager.createNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.DELETED.getCode(), response.statusCode());
        assertTrue(manager.getTasksList().isEmpty(), "Task is not deleted");
    }

    @DisplayName("Should return correct response code if task not found by GET request with id")
    @Test
    public void shouldReturnCorrectResponseCodeIfTaskNotFoundByGetRequestWithId() {
        HttpClient client = HttpClient.newHttpClient();
        int notExistId = 999;
        URI url = URI.create("http://localhost:8080/tasks/" + notExistId);
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

        assertEquals(HttpCodeResponse.NOT_FOUND.getCode(), response.statusCode(), "Expected 404 when task is not found");
        assertEquals(response.body(), "Task with id " + notExistId + " not found",
                "Expected specific error message when task is not found");
    }

    @DisplayName("Should return correct response code if task overlaps with POST request without id")
    @Test
    public void shouldReturnCorrectResponseCodeIfTaskIfTaskOverlapsWithPostRequestWithoutId() {
        task = new Task("Task 1", "Description 1",
                Status.NEW, fixedTime, 30);
        manager.createNewTask(task);

        Task overlappingTask = new Task("Overlapping Task", "Description 2",
                Status.NEW, fixedTime, 30);
        String taskJson = gson.toJson(overlappingTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            logger.log(Level.SEVERE, "Failed to send response", serverResponseError);
        }

        assertEquals(HttpCodeResponse.OVERLAPS.getCode(), response.statusCode(), "Expected 406 when task time overlaps with an existing task");
    }
}

