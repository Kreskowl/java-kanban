package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.server.BaseHttpHandler;
import ru.yandex.practicum.server.HttpTaskServer;
import ru.yandex.practicum.util.Managers;

import java.io.IOException;
import java.time.Duration;

public abstract class HttpTaskServerTestBase {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected Gson gson;

    public HttpTaskServerTestBase() throws IOException {
        this.manager = Managers.getDefault();
        this.server = new HttpTaskServer(manager);
        this.gson = server.getGson();
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubTasks();
        manager.clearEpics();
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }
}
