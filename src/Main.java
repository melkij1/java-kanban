import http.HttpTaskServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new Managers().getTaskManager();

        Task task = new Task("Задача 1", "Описание задачи 1", LocalDateTime.of(2024, 1,1,0,0), 1000);
        Task task2 = new Task("Задача 1", "Описание задачи 1", LocalDateTime.of(2025, 1,1,0,0), 1000);
        taskManager.createTask(task);
        taskManager.createTask(task2);
        Epic epic = new Epic("Эпик 1", "Описание эпика 2");
        taskManager.createEpic(epic);
        SubTask subTask  = new SubTask("Подзадача  1",  "Описание подзадачи  3", 2, LocalDateTime.of(2024, 2,3,0,0), 1000);
        taskManager.createSubTask(subTask);

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);


        httpTaskServer.start();


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