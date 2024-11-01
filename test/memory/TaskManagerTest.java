package memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected SubTask subTask;
    protected Epic epic;
    protected File tempFile;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        try {
            tempFile = Files.createTempFile("Save", ".tmp").toFile();
        } catch (IOException fileIsNotCreated) {
            System.out.println("Can`t create file " + tempFile.getAbsolutePath());
        }
        manager = createManager();
        task = new Task("test", "test", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 10, 0), 30);
        epic = new Epic("epic", "test");
    }

    @Test
    @DisplayName("Should add objects in right list")
    public void shouldAddObjectsInRightList() {
        manager.createNewEpic(epic);
        manager.createNewTask(task);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);

        assertEquals(task, manager.getTasksList().get(0), "does not add task to list");
        assertEquals(subTask, manager.getSubTasksList().get(0), "does not add subtask to list");
        assertEquals(epic, manager.getEpicsList().get(0), "does not add epic to list");
    }

    @Test
    @DisplayName("Same class objects with same id should be equal")
    public void sameClassObjectsWithSameIdShouldBeEqual() {
        manager.createNewEpic(epic);
        manager.createNewTask(task);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);

        final Task savedTask = manager.getTaskById(task.getId())
                .orElseThrow(() -> new AssertionFailedError("Task not found"));
        final Epic savedEpic = manager.getEpicById(epic.getId())
                .orElseThrow(() -> new AssertionFailedError("Epic not found"));
        final SubTask savedSubTask = manager.getSubTaskById(subTask.getId())
                .orElseThrow(() -> new AssertionFailedError("Subtask not found"));

        assertEquals(task, savedTask, "tasks are not equally");
        assertEquals(subTask, savedSubTask, "subtasks are not equally");
        assertEquals(epic, savedEpic, "epics are not equally");
    }

    @Test
    @DisplayName("Should return actual list of objects after deleting")
    public void shouldReturnActualListOfObjectsAfterDeleting() {
        manager.createNewEpic(epic);
        manager.createNewTask(task);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);

        final Task task2 = new Task("task", "test delete by id", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 12, 48), 30);
        manager.createNewTask(task2);
        final Epic epic2 = new Epic("second epic", "test");
        manager.createNewEpic(epic2);
        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 2", Status.IN_PROGRESS, epic2.getId(),
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        manager.createNewSubTask(subTask2);

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        final List<Task> tasks = manager.getTasksList();
        final List<SubTask> subTasks = manager.getSubTasksList();
        final List<Epic> epics = manager.getEpicsList();

        assertNotNull(tasks, "does not return list");
        assertNotNull(subTasks, "does not return list");
        assertNotNull(epics, "does not return list");

        for (Task task : tasks) {
            assertNotEquals(task2, task, "task does not delete correctly");
        }

        for (SubTask subTask :
                subTasks) {
            assertNotEquals(subTask2, subTask, "subtask does not delete correctly");
        }

        for (Epic epic : epics) {
            assertNotEquals(epic2, epic, "epic does not delete correctly");
        }

        manager.clearTasks();
        manager.clearEpics();

        assertEquals(manager.getTasksList().size(), 0, "does not remove objects from list");
        assertEquals(manager.getSubTasksList().size(), 0, "does not remove objects from list if delete all epics");
        assertEquals(manager.getEpicsList().size(), 0, "does not remove objects from list");
    }

    @Test
    @DisplayName("Should return correct list of assigned subtasks")
    public void shouldReturnCorrectListOfAssignedSubtasks() {
        manager.createNewEpic(epic);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);
        final Epic epic2 = new Epic("second epic", "test");
        manager.createNewEpic(epic2);
        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 1", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        final SubTask subTask3 = new SubTask("subtask", "belongs to epic 2", epic2.getId(), Status.NEW,
                epic2.getId(), LocalDateTime.of(2024, 10, 29, 22, 0), 30);
        manager.createNewSubTask(subTask2);
        manager.createNewSubTask(subTask3);

        List<SubTask> assignedToEpic = epic.getAssignedSubTasks();
        List<SubTask> assignedToEpic2 = epic2.getAssignedSubTasks();

        assertEquals(assignedToEpic.size(), 2, "incorrect number of subtasks");
        assertEquals(assignedToEpic2.size(), 1, "incorrect number of subtasks");

        for (SubTask subTask : assignedToEpic) {
            assertEquals(subTask.getEpicId(), epic.getId(), "subtasks does not delete correctly");
        }
    }

    @Test
    @DisplayName("Should update epic status")
    public void shouldUpdateEpicStatus() {
        manager.createNewEpic(epic);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "not correct status update");

        manager.clearSubTasks();
        assertEquals(epic.getStatus(), Status.NEW, "not correct status update");

        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 1", Status.DONE, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        manager.createNewSubTask(subTask2);
        assertEquals(epic.getStatus(), Status.DONE, "not correct status update");

        final SubTask subTask3 = new SubTask("subtask", "belongs to epic 1", Status.NEW, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 20, 0), 30);
        manager.createNewSubTask(subTask3);
        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "not correct status update");

        manager.deleteSubtaskById(subTask2.getId());
        assertEquals(epic.getStatus(), Status.NEW, "not correct status update");
    }

    @Test
    @DisplayName("Should correctly update object info")
    public void shouldCorrectlyUpdateObjectInfo() {
        manager.createNewEpic(epic);
        manager.createNewTask(task);
        subTask = new SubTask("test2", "test2", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 16, 0), 30);
        manager.createNewSubTask(subTask);
        final Task task2 = new Task("update task", "test update", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 10, 29, 12, 48), 30);
        final Epic epic2 = new Epic("update epic", "test update");
        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 1", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        task2.setId(task.getId());
        subTask2.setId(subTask.getId());
        epic2.setId(epic.getId());

        manager.updateTask(task2);
        manager.updateSubTask(subTask2);
        manager.updateEpic(epic2);
        final List<Task> tasks = manager.getTasksList();
        final List<SubTask> subTasks = manager.getSubTasksList();
        final List<Epic> epics = manager.getEpicsList();

        assertEquals(tasks.size(), 1, "add task instead of update");
        assertEquals(subTasks.size(), 1, "add subtask instead of update");
        assertEquals(epics.size(), 1, "add epic instead of update");
        assertEquals(task2, tasks.get(0), "task is not update");
        assertEquals(subTask2, subTasks.get(0), "subtask is not update");
        assertEquals(epic2, epics.get(0), "epic is not update");
    }

    @Test
    @DisplayName("Should detect full overlap")
    public void shouldDetectFullOverlap() {
        Task task1 = new Task("Task 1", "Full overlap", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 10, 0), 60);
        Task task2 = new Task("Task 2", "Overlapping", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 10, 30), 30);

        manager.createNewTask(task1);
        assertThrows(IllegalArgumentException.class, () -> manager.createNewTask(task2),
                "Expected to throw exception due to full overlap, but it didn't");
    }

    @Test
    @DisplayName("Should detect partial overlap")
    public void shouldDetectPartialOverlap() {
        Task task1 = new Task("Task 1", "Partial overlap", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 10, 0), 60);
        Task task2 = new Task("Task 2", "Overlapping", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 9, 30), 60);

        manager.createNewTask(task1);
        assertThrows(IllegalArgumentException.class, () -> manager.createNewTask(task2),
                "Expected to throw exception due to partial overlap, but it didn't");
    }

    @Test
    @DisplayName("Should not detect overlap if intervals are adjacent")
    public void shouldNotDetectAdjacentIntervalsAsOverlap() {
        Task task1 = new Task("Task 1", "Adjacent intervals", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 10, 0), 60);
        Task task2 = new Task("Task 2", "Non-overlapping", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 11, 0), 60);

        manager.createNewTask(task1);
        assertDoesNotThrow(() -> manager.createNewTask(task2),
                "Expected no exception for adjacent intervals, but got an overlap error");
    }

    @Test
    @DisplayName("Should not detect overlap if intervals do not overlap at all")
    public void shouldNotDetectOverlapWhenIntervalsAreSeparate() {
        Task task1 = new Task("Task 1", "Separate intervals", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 10, 0), 60);
        Task task2 = new Task("Task 2", "Non-overlapping", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 12, 0), 60);

        manager.createNewTask(task1);
        assertDoesNotThrow(() -> manager.createNewTask(task2),
                "Expected no exception for non-overlapping intervals, but got an overlap error");
    }

    @Test
    @DisplayName("Should return tasks sorted by startTime in ascending order")
    public void shouldReturnTasksSortedByStartTime() {
        manager.createNewTask(task);
        manager.createNewEpic(epic);

        subTask = new SubTask("test2", "should be first", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 8, 0), 30);
        Task task2 = new Task("Task 2", "should be last", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        manager.createNewTask(task2);
        manager.createNewSubTask(subTask);
        TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size(), "The size of prioritized tasks is incorrect");

        Task[] tasks = prioritizedTasks.toArray(new Task[0]);
        assertEquals(subTask, tasks[0], "First task should be subtask");
        assertEquals(task, tasks[1], "Second task should be task");
        assertEquals(task2, tasks[2], "Third task should be task2");
    }

    @Test
    @DisplayName("Should return tasks sorted by startTime and exclude tasks without startTime")
    public void shouldReturnTasksSortedByStartTimeExcludingNullStartTime() {
        manager.createNewTask(task);
        manager.createNewEpic(epic);

        subTask = new SubTask("test2", "should be first", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2024, 10, 29, 8, 0), 30);
        Task task2 = new Task("Task 2", "should be last", Status.NEW,
                LocalDateTime.of(2024, 10, 29, 18, 0), 30);
        Task withOutTime = new Task("task no start time", "no start time task",
                Status.NEW, null, 0);
        manager.createNewTask(task2);
        manager.createNewTask(withOutTime);
        manager.createNewSubTask(subTask);
        TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size()
                ,"The size of prioritized tasks is incorrect (should exclude tasks without startTime)");

        Task[] tasks = prioritizedTasks.toArray(new Task[0]);
        assertEquals(subTask, tasks[0], "First task should be subtask");
        assertEquals(task, tasks[1], "Second task should be task");
        assertEquals(task2, tasks[2], "Third task should be task2");
    }
}
