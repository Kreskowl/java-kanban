package server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtasksHandlerTest extends HttpTaskServerTestBase{

    private SubTask subTask;
    private final LocalDateTime fixedTime = LocalDateTime.of(2024, 10, 9, 10, 0);

    public SubtasksHandlerTest() throws IOException {
        super();
    }

    @DisplayName("Should add subtask with POST request without id")
    @Test
    public void shouldAddSubTaskWithPostRequestWithoutId() {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createNewEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), fixedTime, 10);
        subTask.setId(2);
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.getMessage();
        }
        assertEquals(201, response.statusCode());

        assertEquals(1, manager.getSubTasksList().size(), "Subtask not add in list");
        assertEquals(subTask, manager.getSubTasksList().get(0), "Added subtask not equals with posted");
    }

    @DisplayName("Should update subtask with POST request with id")
    @Test
    public void shouldUpdateSubTaskWithPostRequestWithId() {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createNewEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), fixedTime, 10);
        manager.createNewSubTask(subTask);
        subTask.setStatus(Status.IN_PROGRESS);
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.printStackTrace();
        }

        assertEquals(201, response.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = null;
        try {
            getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Task updatedSubTask = gson.fromJson(getResponse.body(), SubTask.class);
        assertEquals(subTask, updatedSubTask, "Subtask was not updated");
    }

    @DisplayName("Should return subtasks list with GET request without id")
    @Test
    public void shouldReturnSubTasksListWithGetRequestWithoutId() {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createNewEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), fixedTime, 10);
        manager.createNewSubTask(subTask);
        List<SubTask> subTasks = manager.getSubTasksList();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
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

        assertEquals(200, response.statusCode());
        assertEquals(1, manager.getSubTasksList().size(), "Subtask not add in list");
        assertEquals(subTasks, manager.getSubTasksList(), "Lists not the same");
    }

    @DisplayName("Should return subtask with GET request with id")
    @Test
    public void shouldReturnSubTaskWithGetRequestWithId() {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createNewEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), fixedTime, 10);
        manager.createNewSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
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

        assertEquals(200, response.statusCode());

        Task requestedTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask, requestedTask, "Subtask in list is not equal with requested");
    }

    @DisplayName("Should delete subtask with DELETE request")
    @Test
    public void shouldDeleteSubTaskWithDeleteRequest() {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createNewEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), fixedTime, 10);
        manager.createNewSubTask(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.printStackTrace();
        }

        assertEquals(204, response.statusCode());
        assertTrue(manager.getSubTasksList().isEmpty(), "Subtask in not deleted");
    }

    @DisplayName("Should return 404 response code if subtask not found by GET request with id")
    @Test
    public void shouldReturnCorrectResponseCodeIfSubtaskNotFoundByGetRequestWithId() {
        HttpClient client = HttpClient.newHttpClient();
        int notExistId = 999;
        URI url = URI.create("http://localhost:8080/subtasks/" + notExistId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.getMessage();
        }

        assertEquals(404, response.statusCode(), "Expected 404 when subtask is not found");
        assertEquals(response.body(), "Subtask with id " + notExistId + " not found",
                "Expected specific error message when subtask is not found");
    }

    @DisplayName("Should return 406 response code if subtask overlaps with existing subtask when creating new subtask")
    @Test
    public void shouldReturnCorrectResponseCodeIfSubtaskOverlapsWithExistingSubtask() {
        Epic epic = new Epic("Epic", "Epic Description");
        manager.createNewEpic(epic);

        subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), fixedTime, 30);
        manager.createNewSubTask(subTask);

        SubTask overlappingSubtask = new SubTask("Overlapping Subtask", "Description 2",
                Status.NEW, epic.getId(), fixedTime, 30);
        String subtaskJson = gson.toJson(overlappingSubtask);;

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.getMessage();
        }

        assertEquals(406, response.statusCode(), "Expected 406 if subtask overlaps with an existing subtask");
    }
}
