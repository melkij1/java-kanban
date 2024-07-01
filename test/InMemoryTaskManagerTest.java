import org.junit.jupiter.api.BeforeEach;
import service.impl.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @BeforeEach
    void setUp() {
        super.taskManager = new InMemoryTaskManager();
        initTasks();
    }

}