import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

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
        task = new Task("test", "test", Status.NEW);
        epic = new Epic("epic", "test");
        subTask = new SubTask("test2", "test2", 1, Status.IN_PROGRESS);
        test.createNewEpic(epic);
        test.createNewTask(task);
        test.createNewSubTask(subTask);

    }

    @Test
    public void shouldAddTaskInHistory() {
        List<Task> historyTest = test.getHistory();

        assertEquals(historyTest.size(), 0, "initial capacity > 0");

        test.getTaskById(task.getId());
        historyTest = test.getHistory();
        assertEquals(historyTest.size(), 1, "task does not add in the list");
    }

    @Test
    public void shouldAddTaskInTheEndOfHistory() {
        List<Task> historyTest;

        test.getEpicById(epic.getId());
        test.getSubTaskById(subTask.getId());
        historyTest = test.getHistory();

        assertEquals(historyTest.size(), 2, "size is not correct");
        assertEquals(historyTest.get(historyTest.size() - 1), subTask, "subtask added at wrong place");
    }

    @Test
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