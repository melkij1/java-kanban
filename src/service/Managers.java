package service;

import service.impl.InMemoryHistoryManager;
import service.impl.InMemoryTaskManager;

public class Managers {

    public static TaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
