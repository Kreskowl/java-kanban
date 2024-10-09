import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.manager.ManagerSaveException;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager test;
    private File tempFile;
    private Task task;
    private SubTask subTask;
    private Epic epic;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("SavedTasksTestFile-", ".tmp").toFile();
        test = new FileBackedTaskManager(tempFile);
        task = new Task("test", "test", Status.NEW);
        epic = new Epic("epic", "test");
        subTask = new SubTask("test2", "test2", epic.getId(), Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Should create empty save file")
    public void shouldCreateEmptySaveFile() {

        assertTrue(tempFile.exists() && tempFile.isFile(), "does not create save file");

        try {
            long fileSize = Files.size(tempFile.toPath());
            assertEquals(0, fileSize, "File is not empty");
        } catch (IOException e) {
            throw new ManagerSaveException("File does not exist" + tempFile.getPath());
        }
    }

    @Test
    @DisplayName("Should be a header in save file")
    public void shouldBeAHeaderInSaveFile() {
        test.createNewTask(task);

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String expected = "id, type, name, status, description, epicId";
            String actual = reader.readLine();
            assertEquals(expected, actual, "first line is not a header");
        } catch (IOException e) {
            throw new ManagerSaveException("Can`t read the file " + tempFile.getPath());
        }
    }

    @Test
    @DisplayName("Should add in order created objects in save file")
    public void shouldAddInOrderCreatedObjectsInSaveFile() {
        test.createNewEpic(epic);
        test.createNewTask(task);
        test.createNewSubTask(subTask);

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            reader.readLine();

            String savedEpic = reader.readLine();
            assertEquals(epic.toString(), savedEpic, "does not save epic in right place");

            String savedTask = reader.readLine();
            assertEquals(task.toString(), savedTask, "does not save task in right place");

            String savedSubTask = reader.readLine();
            assertEquals(subTask.toString(), savedSubTask, "does not save subtask in right place");
        } catch (IOException e) {
            throw new ManagerSaveException("Can`t read the file " + tempFile.getPath());
        }
    }

    @Test
    @DisplayName("Should load info from save file")
    public void shouldLoadInfoFromSaveFile() {
        test.createNewEpic(epic);
        test.createNewTask(task);
        test.createNewSubTask(subTask);
        FileBackedTaskManager result = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(test.getTasksList(), result.getTasksList(), "Does not load info about tasks");
        assertEquals(test.getSubTasksList(), result.getSubTasksList(), "Does not load info about subtasks");
        assertEquals(test.getEpicsList(), result.getEpicsList(), "Does not load info about epics");
    }
}
