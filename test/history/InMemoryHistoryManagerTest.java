package history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.Util.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private TaskManager test;
    private Task task;
    private SubTask subTask;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        test = Managers.getDefault();
        task = new Task("test", "test", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 14, 48), 30);
        epic = new Epic("epic", "test");
        test.createNewEpic(epic);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS,  epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0),30);
        test.createNewTask(task);
        test.createNewSubTask(subTask);

    }

    @Test
    @DisplayName("Should return empty history initially")
    public void shouldReturnEmptyHistoryInitially() {
        List<Task> historyTest = test.getHistory();
        assertEquals(0, historyTest.size(), "History should be empty initially");
    }

    @Test
    @DisplayName("Should not add duplicate entries in history")
    public void shouldNotAddDuplicateEntriesInHistory() {
        test.getTaskById(task.getId());
        test.getTaskById(task.getId());
        List<Task> historyTest = test.getHistory();
        assertEquals(1, historyTest.size(), "Duplicate entries were added to history");
    }

    @Test
    @DisplayName("Should add task in history")
    public void shouldAddTaskInHistory() {
        List<Task> historyTest = test.getHistory();

        assertEquals(historyTest.size(), 0, "initial capacity > 0");

        test.getTaskById(task.getId());
        historyTest = test.getHistory();
        assertEquals(historyTest.size(), 1, "task does not add in the list");
    }

    @Test
    @DisplayName("Should add task at the end of history")
    public void shouldAddTaskInTheEndOfHistory() {
        List<Task> historyTest;

        test.getEpicById(epic.getId());
        test.getSubTaskById(subTask.getId());
        historyTest = test.getHistory();

        assertEquals(historyTest.size(), 2, "size is not correct");
        assertEquals(historyTest.get(historyTest.size() - 1), subTask, "subtask added at wrong place");
    }

    @Test
    @DisplayName("Should delete object from the history")
    public void shouldDeleteObjectFromTheHistory() {
        List<Task> historyTest;

        test.getEpicById(epic.getId());
        test.getSubTaskById(subTask.getId());
        test.getTaskById(task.getId());
        test.deleteSubtaskById(subTask.getId());
        historyTest = test.getHistory();

        assertEquals(historyTest.size(), 2, "does not delete object from the list");
        assertEquals(historyTest.get(historyTest.size() - 1), task, "wrong order of objects after deleting");
    }

    @Test
    @DisplayName("Should update object place in the history and delete its copy after get method")
    public void shouldUpdateObjectPlaceInTheHistoryAndDeleteItsCopyAfterGetMethod() {
        List<Task> historyTest;

        test.getTaskById(task.getId());
        test.getSubTaskById(subTask.getId());
        test.getEpicById(epic.getId());
        test.getTaskById(task.getId());
        historyTest = test.getHistory();

        assertEquals(historyTest.size(), 3, "add the object without deleting its copy");
        assertEquals(historyTest.get(0), subTask, "does not delete old object");
        assertEquals(historyTest.get(historyTest.size() - 1), task, "does not update place of the object");
    }

    @Test
    @DisplayName("Should delete assigned subtasks after removing epic from history")
    public void shouldDeleteAssignedSubtasksAfterRemovingEpicFromHistory() {
        List<Task> historyTest;

        test.getTaskById(task.getId());
        test.getSubTaskById(subTask.getId());
        test.getEpicById(epic.getId());
        test.deleteEpicById(epic.getId());
        historyTest = test.getHistory();

        assertEquals(historyTest.size(), 1, "only epic was deleted");
        assertEquals(historyTest.get(0), task, "wrong object was deleted");
    }
}
