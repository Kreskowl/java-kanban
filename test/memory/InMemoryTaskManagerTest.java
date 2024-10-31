package memory;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.task.Task;

import java.util.Collections;
import java.util.List;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createManager() {
        HistoryForTest test = new HistoryForTest();
        return new InMemoryTaskManager(test);
    }

    private static class HistoryForTest implements HistoryManager {

        @Override
        public void add(Task task) {
        }

        @Override
        public void remove(int id) {

        }

        @Override
        public List<Task> getHistory() {
            return Collections.emptyList();
        }
    }
}