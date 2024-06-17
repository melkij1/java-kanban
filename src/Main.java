import model.Epic;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        TaskManager fileBackedManager = new Managers().getDefaultFiles();

        Task task = new Task("Задача 1", "Описание задачи 1");
        fileBackedManager.createTask(task);
        Epic epic = new Epic("Эпик 1", "Описание эпика 2");
        fileBackedManager.createEpic(epic);
        SubTask subTask  = new SubTask("Подзадача  1",  "Описание подзадачи  3", 1);
        fileBackedManager.createSubTask(subTask);

        //получим файл по пути resources/java-kanban.csv вида
//        id,type,name,status,description,epic_id
//        0,TASK,Задача 1,NEW,Описание задачи 1
//        1,EPIC,Эпик 1,NEW,Описание эпика 2
//        2,SUBTASK,Подзадача  1,NEW,Описание подзадачи  3,1

        //получаем данные из файла и создаем задачи
//        File file  = new File("resources/java-kanban.csv");
//        FileBackedTaskManager fileBackedManager = new FileBackedTaskManager(file);
//
//        fileBackedManager.loadFromFile(file);
//
//        fileBackedManager.getTasks();
//
//        fileBackedManager.getEpics();
//
//        fileBackedManager.getSubTasks();

    }
}