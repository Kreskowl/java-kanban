package server;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class EpicsHandlerTest extends HttpTaskServerTestBase {
    private Epic epic;

    public EpicsHandlerTest() throws IOException {
        super();
    }

    @DisplayName("Should add epic with POST request without id")
    @Test
    public void shouldAddEpicWithPostRequestWithoutId() {
        epic = new Epic("Test", "Testing epic");
        String epicJson = gson.toJson(epic);
        epic.setId(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.getMessage();
        }

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getEpicsList().size(), "Epic is not added in list");
        assertEquals(epic, manager.getEpicsList().get(0), "Epic in list is not equal with posted");
    }

    @DisplayName("Should update epic with POST request with id")
    @Test
    public void shouldUpdateEpicWithPostRequestWithId() {
        epic = new Epic("Test", "Testing epic");
        manager.createNewEpic(epic);
        epic.setName("new name");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
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

        Task updatedEpic = gson.fromJson(getResponse.body(), Epic.class);
        assertEquals(epic, updatedEpic, "Epic was not updated");
    }

    @DisplayName("Should return epics list with GET request without id")
    @Test
    public void shouldReturnEpicsListWithGetRequestWithoutId() {
        epic = new Epic("Test", "Testing epic");
        Epic epic2 = new Epic("Test 2", "testing epic 2");
        manager.createNewEpic(epic);
        List<Epic> epics = manager.getEpicsList();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
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

        Type listOfEpicType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> serverEpics = gson.fromJson(response.body(), listOfEpicType);

        assertEquals(epics.size(), serverEpics.size(), "Size of the list is not correct");
        assertEquals(epics, serverEpics, "List not matched");
    }

    @DisplayName("Should return epic with GET request with id")
    @Test
    public void shouldReturnEpicWithGetRequestWithId() {
        epic = new Epic("Test", "Testing epic");
        manager.createNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
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

        Task requestedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic, requestedEpic, "Get wrong epic");
    }

    @DisplayName("Should delete epic with DELETE request")
    @Test
    public void shouldDeleteEpicWithDeleteRequest() {
        epic = new Epic("Test", "Testing epic");
        manager.createNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
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
        assertTrue(manager.getEpicsList().isEmpty(), "Epic is not deleted");
    }

    @DisplayName("Should return correct response code if epic not found by GET request with id")
    @Test
    public void shouldReturnCorrectResponseCodeIfEpicNotFoundByGetRequestWithId() {
        HttpClient client = HttpClient.newHttpClient();
        int notExistId = 999;
        URI url = URI.create("http://localhost:8080/epics/" + notExistId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            fail("Request failed: " + serverResponseError.getMessage());
        }

        assertEquals(404, response.statusCode(), "Expected 404 when epic is not found");
        assertEquals(response.body(), "Epic with id " + notExistId + " not found",
                "Expected specific error message when epic is not found");
    }

    @DisplayName("Should return correct list of subtasks with GET request when path ends with /subtasks")
    @Test
    public void shouldReturnCorrectListOfSubTasksWithGetRequestWhenPathEndsWithSubtasks() {
        epic = new Epic("Test", "Testing epic");
        manager.createNewEpic(epic);

        SubTask subTask = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(), LocalDateTime.now(), 30);
        manager.createNewSubTask(subTask);

        SubTask subTask2 = new SubTask("Subtask 1", "Description 1",
                Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 10, 10, 10, 10, 10), 30);
        manager.createNewSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            serverResponseError.printStackTrace();
        }

        assertEquals(200, response.statusCode());

        Type listOfSubtaskType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<Epic> serverAssignedSubtasks = gson.fromJson(response.body(), listOfSubtaskType);

        assertEquals(manager.getAssignedSubTasks(epic.getId()).size(), serverAssignedSubtasks.size(), "Should be 2 subtasks in the list");
        assertIterableEquals(manager.getAssignedSubTasks(epic.getId()), serverAssignedSubtasks, "Lists are not equal");
    }

    @DisplayName("Should return correct response code with GET request when path ends with /subtasks if epic not exist")
    @Test
    public void shouldReturnCorrectResponseCodeWithGetRequestWhenPathEndsWithSubtasksIfEpicNotExist() {
        HttpClient client = HttpClient.newHttpClient();
        int notExistId = 999;
        URI url = URI.create("http://localhost:8080/epics/" + notExistId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException serverResponseError) {
            fail("Request failed: " + serverResponseError.getMessage());
        }

        assertEquals(404, response.statusCode(), "Expected 404 when epic is not found");
        assertEquals(response.body(), "Epic with id " + notExistId + " not found",
                "Expected specific error message when epic is not found");
    }
}
