package memory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.Util.DateAndTimeFormatUtil;
import ru.yandex.practicum.Util.Managers;
import ru.yandex.practicum.manager.FileBackedTaskManager;
import ru.yandex.practicum.manager.ManagerSaveException;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(Managers.getDefaultHistory(), tempFile);
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription() + "," + task.getEpicId() + ","
                + DateAndTimeFormatUtil.formatDateTime(task.getStartTime()) + ","
                + DateAndTimeFormatUtil.formatDurationTime(task.getDuration());
    }

    @DisplayName("Should create empty save file")
    @Test
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
        manager.createNewTask(task);

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String expected = "id, type, name, status, description, epicId, start time, duration";
            String actual = reader.readLine();
            assertEquals(expected, actual, "first line is not a header");
        } catch (IOException e) {
            throw new ManagerSaveException("Can`t read the file " + tempFile.getPath());
        }
    }

    @Test
    @DisplayName("Should add in order created objects in save file")
    public void shouldAddInOrderCreatedObjectsInSaveFile() {
        manager.createNewEpic(epic);
        manager.createNewTask(task);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            reader.readLine();

            String savedEpic = reader.readLine();
            assertEquals(toString(epic), savedEpic, "does not save epic in right place");

            String savedTask = reader.readLine();
            assertEquals(toString(task), savedTask, "does not save task in right place");

            String savedSubTask = reader.readLine();
            assertEquals(toString(subTask), savedSubTask, "does not save subtask in right place");
        } catch (IOException e) {
            throw new ManagerSaveException("Can`t read the file " + tempFile.getPath());
        }
    }

    @Test
    @DisplayName("Should load info from save file")
    public void shouldLoadInfoFromSaveFile() {
        manager.createNewEpic(epic);
        manager.createNewTask(task);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);
        FileBackedTaskManager result = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(manager.getTasksList(), result.getTasksList(), "Does not load info about tasks");
        assertEquals(manager.getSubTasksList(), result.getSubTasksList(), "Does not load info about subtasks");
        assertEquals(manager.getEpicsList(), result.getEpicsList(), "Does not load info about epics");
    }
}
