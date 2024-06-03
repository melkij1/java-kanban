import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;


class ManagersTest {

    @Test
    public void taskManagerClassAlwaysReturnsInitializedManager() {
        TaskManager taskManager = new Managers().getTaskManager();
        Assertions.assertNotNull(taskManager);
    }
}