import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

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
    @DisplayName("Should add objects in right list")
    public void shouldAddObjectsInRightList() {
        assertEquals(task, test.getTaskById(task.getId()),"does not add task to list");
        assertEquals(subTask, test.getSubTaskById(subTask.getId()), "does not add subtask to list");
        assertEquals(epic, test.getEpicById(epic.getId()),"does not add epic to list");
    }

    @Test
    @DisplayName("Same class objects with same id should be equal")
    public void sameClassObjectsWithSameIdShouldBeEqual() {

        final Task savedTask = test.getTaskById(task.getId());
        final Epic savedEpic = test.getEpicById(epic.getId());
        final SubTask savedSubTask = test.getSubTaskById(subTask.getId());

        assertEquals(task, savedTask, "tasks are not equally");
        assertEquals(subTask, savedSubTask, "subtasks are not equally");
        assertEquals(epic, savedEpic, "epics are not equally");
    }

    @Test
    @DisplayName("Should return actual list of objects after deleting")
    public void shouldReturnActualListOfObjectsAfterDeleting() {

        final Task task2 = new Task("task", "test delete by id", Status.NEW);
        test.createNewTask(task2);
        final Epic epic2 = new Epic("second epic", "test");
        test.createNewEpic(epic2);
        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 1", epic2.getId(), Status.DONE);
        test.createNewSubTask(subTask2);

        test.deleteTaskById(task2.getId());
        test.deleteEpicById(epic2.getId());

        final List<Task> tasks = test.getTasksList();
        final List<SubTask> subTasks = test.getSubTasksList();
        final List<Epic> epics = test.getEpicsList();

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
            assertNotEquals(epic2, epic,"epic does not delete correctly");
        }

        test.clearTasks();
        test.clearEpics();

        assertEquals(test.getTasksList().size(), 0, "does not remove objects from list");
        assertEquals(test.getSubTasksList().size(), 0 , "does not remove objects from list if delete all epics");
        assertEquals(test.getEpicsList().size(), 0, "does not remove objects from list");
    }

    @Test
    @DisplayName("Should return correct list of assigned subtasks")
    public void shouldReturnCorrectListOfAssignedSubtasks() {
        final Epic epic2 = new Epic("second epic", "test");
        test.createNewEpic(epic2);
        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 1", epic.getId(), Status.NEW);
        final SubTask subTask3 = new SubTask("subtask", "belongs to epic 2", epic2.getId(), Status.NEW);
        test.createNewSubTask(subTask2);
        test.createNewSubTask(subTask3);

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

        assertEquals(epic.getStatus(), Status.IN_PROGRESS, "not correct status update");

        test.clearSubTasks();
        assertEquals(epic.getStatus(), Status.NEW,"not correct status update");

        final SubTask subTask2 = new SubTask("subtask", "belongs to epic 1", epic.getId(), Status.DONE);
        test.createNewSubTask(subTask2);
        assertEquals(epic.getStatus(), Status.DONE,"not correct status update");

        test.deleteSubtaskById(subTask2.getId());
        assertEquals(epic.getStatus(), Status.NEW,"not correct status update");
    }

    @Test
    @DisplayName("Should correctly update object info")
    public void shouldCorrectlyUpdateObjectInfo() {
        final Task task2 = new Task("update task", "test update", Status.IN_PROGRESS);
        final Epic epic2 = new Epic("update epic", "test update");
        final SubTask subTask2 = new SubTask("update subtask", "update",
                epic.getId(), Status.IN_PROGRESS);
        task2.setId(task.getId());
        subTask2.setId(subTask.getId());
        epic2.setId(epic.getId());

        test.updateTask(task2);
        test.updateSubTask(subTask2);
        test.updateEpic(epic2);
        final List<Task> tasks = test.getTasksList();
        final List<SubTask> subTasks = test.getSubTasksList();
        final List<Epic> epics = test.getEpicsList();

        assertEquals(tasks.size(), 1,"add task instead of update");
        assertEquals(subTasks.size(), 1, "add subtask instead of update");
        assertEquals(epics.size(), 1, "add epic instead of update");
        assertEquals(task2, tasks.get(0), "task is not update");
        assertEquals(subTask2, subTasks.get(0), "subtask is not update");
        assertEquals(epic2, epics.get(0), "epic is not update");
    }
}