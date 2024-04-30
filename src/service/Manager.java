package service;

import service.impl.TaskManagerServiceImpl;

public class Manager {

    public static TaskManagerService getTaskManager(){
        return new TaskManagerServiceImpl();
    }
}
