package test.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Manager;
import service.TaskManager;


class ManagerTest {

    @Test
    public void taskManagerClassAlwaysReturnsInitializedManager() {
        TaskManager taskManager = new Manager().getTaskManager();
        Assertions.assertNotNull(taskManager);
    }
}