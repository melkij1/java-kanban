import enums.TaskStatus;
import exceptions.TaskException;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    protected final LocalDateTime DATE = LocalDateTime.of(2024,1,1,0,0);
    protected final int EPIC_ID = 1;
    protected Task task1;
    protected Epic epic2;
    protected SubTask subTask3;


    protected void initTasks() {
        task1 = new Task("Задача 1", "Описание задачи 1", DATE, 1000);
        taskManager.createTask(task1);

        epic2 = new Epic("Эпик 1", "Описание эпика 2");
        taskManager.createEpic(epic2);

        subTask3 = new SubTask("Подзадача  1",  "Описание подзадачи  3", EPIC_ID, DATE.plusDays(2), 1000);
        taskManager.createSubTask(subTask3);
    }


    @Test
    void createTask() {
        Task taskFind = taskManager.getTaskById(0);

        assertNotNull(taskFind, "Задача не найдена.");
        assertNotNull(taskManager.getTasks(), "Задачи на возвращаются.");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач.");
        Assertions.assertEquals(0, taskFind.getId(), "Идентификаторы задач не совпадают");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 0)
                .findFirst()
                .orElse(null);
        assertNotNull(taskPriority, "Задача не добавлена в список приоритизации");
        Assertions.assertEquals(taskPriority, taskFind, "В список приоритизации добавлена неверная задача");
    }

    @Test
    void testCreateEpic() {
        Epic epicFind = taskManager.getEpicById(1);
        assertNotNull(epicFind, "Задача не найдена.");
        assertNotNull(taskManager.getEpics(), "Задачи на возвращаются.");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач.");
        assertNotNull(epicFind.getSubTasks(), "Список подзадач не создан");
        Assertions.assertEquals(TaskStatus.NEW, epicFind.getStatus(), "Статус не равен NEW");
        Assertions.assertEquals(1, epicFind.getId(), "Идентификаторы задач не совпадают");
    }

    @Test
    void testCreateSubTask() {
        Epic epicFind = taskManager.getEpicById(EPIC_ID);
        assertNotNull(epicFind.getStartTime(), "Время эпика не равно null");

        SubTask subTaskFind = taskManager.getSubTaskById(2);
        assertNotNull(subTaskFind, "Задача не найдена.");
        assertNotNull(taskManager.getSubTasks(), "Задачи на возвращаются.");
        Assertions.assertEquals(1, taskManager.getSubTasks().size(), "Неверное количество задач.");
        Assertions.assertEquals(2, subTaskFind.getId(), "Идентификаторы задач не совпадают");

        Task subTaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 2)
                .findFirst()
                .orElse(null);
        assertNotNull(subTaskPriority, "Задача не добавлена в список приоритизации");
        Assertions.assertEquals(subTaskPriority, subTaskFind, "В список приоритизации добавлена неверная задача");
    }

    @Test
    void testCheckEpicStatus() {
        Epic epicFind = taskManager.getEpicById(EPIC_ID);

        SubTask updateTask = new SubTask(2, "Подзадача  1",  "Описание подзадачи  3", TaskStatus.IN_PROGRESS,  EPIC_ID, DATE.plusDays(2), 1000);
        taskManager.updateSubTask(updateTask);

        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epicFind.getStatus(), "Статус не равен IN_PROGRESS");

        SubTask updateTask2 = new SubTask(2, "Подзадача  1",  "Описание подзадачи  3", TaskStatus.DONE,  EPIC_ID, DATE.plusDays(2), 1000);
        taskManager.updateSubTask(updateTask2);


        Assertions.assertEquals(TaskStatus.DONE, epicFind.getStatus(), "Статус не равен DONE");
    }

    @Test
    void getHistory() {
        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubTaskById(2);
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(3, history.size(), "Список истории не верен");
        Assertions.assertEquals(0, history.get(0).getId(), "Задача 0 не добавлена в список истории");
        Assertions.assertEquals(2, history.get(2).getId(), "Задача 0 не добавлена в список истории");
        Assertions.assertEquals(1, history.get(1).getId(), "Задача 0 не добавлена в список истории");

    }

    @Test
    void testGetPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        Assertions.assertEquals(0, prioritizedTasks.get(0).getId(), "Задача 0 не приоритезирована");
        Assertions.assertEquals(2, prioritizedTasks.get(1).getId(), "Задача 0 не приоритезирована");
    }

    @Test
    void testDeleteTaskById(){
        assertNotNull(taskManager.getTasks(), "Список задач пустой");
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач");
        taskManager.deleteTaskById(0);
        Assertions.assertNull(taskManager.getTaskById(0), "Задача не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 0)
                .findFirst()
                .orElse(null);

        Assertions.assertNull(taskPriority, "Задача не удалена из списка приоритизации ");
    }

    @Test
    void testDeleteSubTaskById(){
        assertNotNull(taskManager.getSubTasks(), "Список подзадач пустой");
        Assertions.assertEquals(1, taskManager.getSubTasks().size(), "Неверное количество задач");
        taskManager.deleteSubTaskById(2);
        Assertions.assertNull(taskManager.getSubTaskById(2), "Подзадач не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 2)
                .findFirst()
                .orElse(null);

        Assertions.assertNull(taskPriority, "Подзадача не удалена из списка приоритизации ");
    }

    @Test
    void testDeleteEpicById(){
        assertNotNull(taskManager.getEpics(), "Список подзадач пустой");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач");
        taskManager.deleteEpicById(1);
        Assertions.assertNull(taskManager.getEpicById(1), "Подзадач не удалена");
    }

    @Test
    void testValidate(){
        Task task2 =  new Task("Задача 2", "Описание задачи 2", DATE, 1000);
        Task task3 =  new Task("Задача 3", "Описание задачи 3", DATE, 1000);

        TaskException exception = assertThrows(TaskException.class, () -> {
            taskManager.createTask(task2);
            taskManager.createTask(task3);
        });

        Assertions.assertEquals("Время выполнения задачи пересекается с уже существующим временем. Выберите другую дату", exception.getMessage());
    }
}
