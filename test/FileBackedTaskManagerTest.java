import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    void setUp() {
        System.out.println("Запуск теста");
        System.out.println(taskManager);
        file = new File("./resources/java-test.csv");
        super.taskManager = new FileBackedTaskManager(file);
        initTasks();

        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubTaskById(2);
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

}
