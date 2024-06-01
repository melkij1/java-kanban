package test.service;

import enums.TaskStatus;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Manager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    public void createTaskExpectedAddNewTask() {
        TaskManager taskManager = new Manager().getTaskManager();

        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task1", "Description1");

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getId()));
        Assertions.assertEquals(task2, taskManager.getTaskById(task2.getId()));
    }

    @Test
    public void taskIdConflict() {
        TaskManager taskManager = new Manager().getTaskManager();
        Task task5 = new Task("Task5", "Description5");
        Task task6 = new Task("Task6", "Description6");
        Task task7 = new Task("Task7", "Description7");

        taskManager.createTask(task5);
        taskManager.createTask(task6);
        taskManager.createTask(task7);

        Assertions.assertNotEquals(task5.getId(), task7.getId());
        Assertions.assertNotEquals(task5.getId(), task6.getId());
    }

    @Test
    public void taskImmutability() {
        TaskManager taskManager = new Manager().getTaskManager();
        Task task5 = new Task("Task5", "Description5");

        taskManager.createTask(task5);

        Task updatedTask = taskManager.getTaskById(task5.getId());

        Assertions.assertEquals(task5.getDescription(), updatedTask.getDescription());
        Assertions.assertEquals(task5.getStatus(), updatedTask.getStatus());
    }

    @Test
    public void createdTaskAddByHistoryManager() {
        TaskManager taskManager = new Manager().getTaskManager();

        Task task1 = new Task("Task1", "Description1");

        taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());

        Task task3 = new Task(task1.getId(), "Обновленная задачи 1", "Обновление описания задачи 1", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task3);

        Assertions.assertEquals(task1, taskManager.getHistory().get(task1.getId()));
    }

    @Test
    public void checkClearTaskHashMap() {
        TaskManager taskManager = new Manager().getTaskManager();

        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task2", "Description2");
        Task task3 = new Task("Task3", "Description3");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.removeAllTask();

        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void checkClearEpicHashMap() {
        TaskManager taskManager = new Manager().getTaskManager();

        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic2", "Description2");
        Epic epic3 = new Epic("Epic3", "Description3");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);

        taskManager.removeAllEpic();

        Assertions.assertEquals(0, taskManager.getEpics().size());
    }

    @Test
    public void checkClearSubTaskHashMap() {
        TaskManager taskManager = new Manager().getTaskManager();

        Epic epic1 = new Epic("Epic1", "Description1");

        taskManager.createEpic(epic1);
        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", 0);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 0);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 0);

        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.removeAllSubTask();

        Assertions.assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    public void deleteTaskById_notExistent() {
        TaskManager taskManager = new Manager().getTaskManager();
        Task task1 = new Task("Task1", "Description1");

        taskManager.createTask(task1);

        taskManager.deleteTaskById(task1.getId());

        Assertions.assertEquals(0, taskManager.getTasks().size());
    }


    @Test
    public void updateTaskEquals() {
        TaskManager taskManager = new Manager().getTaskManager();
        Task task1 = new Task("Task1", "Description1");
        Task updatedTask = new Task(task1.getId(), "Обновленная задачи 1", "Обновление описания задачи 1", TaskStatus.IN_PROGRESS);

        taskManager.createTask(task1);

        taskManager.updateTask(updatedTask);

        Assertions.assertEquals(updatedTask, taskManager.getTaskById(0));
    }


    @Test
    public void updateSubTaskEquals() {
        TaskManager taskManager = new Manager().getTaskManager();
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");

        taskManager.createEpic(epic);

        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", 0);

        taskManager.createSubTask(subTask);

        SubTask updatedSubTask = new SubTask(subTask.getId(), "Обновленная подзадача 1", "Обновление описания подзадачи 1", TaskStatus.IN_PROGRESS, subTask.getEpicId());
        // Act
        taskManager.updateSubTask(updatedSubTask);

        // Assert
        Assertions.assertEquals(updatedSubTask, taskManager.getSubTaskById(1));
    }


    @Test
    public  void updateEpicEquals(){
        TaskManager taskManager = new Manager().getTaskManager();
        Epic epic = new Epic("Epic1", "Description1");
        taskManager.createEpic(epic);

        Epic updateEpic = new Epic(epic.getId(), "Обновленная эпика 1", "Обновление описания эпика 1", TaskStatus.IN_PROGRESS, epic.getSubTasks());

        taskManager.updateEpic(updateEpic);

        Assertions.assertEquals(updateEpic, taskManager.getEpicById(epic.getId()));
    }
}