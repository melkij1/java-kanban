import enums.TaskStatus;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    static final LocalDateTime DATE = LocalDateTime.of(2024,1,1,0,0);
    static final LocalDateTime SUB_DATE = LocalDateTime.of(2024,2,1,0,0);

    @Test
    void addHistory() {
        TaskManager taskManager = new Managers().getTaskManager();
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, DATE, 1000);
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", 1, SUB_DATE, 1000);
        taskManager.createSubTask(subTask);
        assertEquals(0, taskManager.getHistory().size(), "История пустая");
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "История  не пустая");
    }

    @Test
    void remove() {
        TaskManager taskManager = new Managers().getTaskManager();
        Task task = new Task("Задача 1", "Описание задачи 1");
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        assertEquals(0, taskManager.getHistory().size(), "История пустая");

        taskManager.getTaskById(task.getId());

        assertEquals(1, taskManager.getHistory().size(), "История  не пустая");

        taskManager.removeAllTask();

        taskManager.getHistory().size();
        assertEquals(0, taskManager.getHistory().size(), "История пустая");


    }

    @Test
    void getHistory() {
        TaskManager taskManager = new Managers().getTaskManager();
        Task task = new Task("Задача 1", "Описание задачи 1");
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createTask(task);
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", 1, DATE, 1000);

        taskManager.createSubTask(subTask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubTaskById(subTask.getId());

        assertEquals(3, taskManager.getHistory().size(), "История  не пустая");

        taskManager.deleteTaskById(task.getId());

        assertEquals(2, taskManager.getHistory().size(), "История  не пустая");

    }
}