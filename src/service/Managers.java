package service;

import service.impl.InMemoryHistoryManager;
import service.impl.InMemoryTaskManager;

import java.io.File;

public class Managers {

    public static TaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFiles() {
        return new FileBackedTaskManager(new File("./resources/java-kanban.csv"));
    }
}
