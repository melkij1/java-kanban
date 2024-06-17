import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private File file;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        System.out.println("Запуск теста");
        taskManager = Managers.getDefaultFiles();

        file = new File("./resources/java-test.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task);
        fileBackedTaskManager.createTask(task);

        Epic epic = new Epic("Эпик 1", "Описание эпика 2");
        fileBackedTaskManager.createEpic(epic);
        taskManager.createEpic(epic);

        SubTask subTask  = new SubTask("Подзадача  1",  "Описание подзадачи  3", 1);
        fileBackedTaskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask);
    }


    @Test
    void loadFromFile(){
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("./resources/java-test.csv"));

        assertEquals(1, fileBackedTaskManager.getTasks().size(), "Количество задач после выгрузки из файла не совпадает");
        assertEquals(taskManager.getTasks(), fileBackedTaskManager.getTasks(), "Список задач не совпадает");

        assertEquals(1, fileBackedTaskManager.getEpics().size(), "Количество эпиков после выгрузки из файла не совпадает");
        assertEquals(taskManager.getEpics(), fileBackedTaskManager.getEpics(), "Список эпиков не совпадает");

        assertEquals(1, fileBackedTaskManager.getSubTasks().size(),  "Количество подзадач после выгрузки из файла не совпадает");
        assertEquals(taskManager.getSubTasks(), fileBackedTaskManager.getSubTasks(), "Список подзадач не совпадает");

    }

    @AfterEach
    void tearDown() {
        if(file.exists()){
            assertTrue(file.delete());
        }
    }

}
