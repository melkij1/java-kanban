import enums.TaskStatus;
import model.Epic;
import model.SubTask;
import model.Task;
import service.Manager;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new Manager().getTaskManager();

        //создаем задачи
        Task task = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");

        taskManager.createTask(task);
        taskManager.createTask(task2);

        //создаем эпик
        Epic epic = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");

        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);


        //создаем подзадачу
        SubTask subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", 2);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 2);
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 2);

        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        //получение задачи по id
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task2.getId());

        //получение подзадачи по id
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getSubTaskById(subTask2.getId());
        taskManager.getSubTaskById(subTask3.getId());

        //получение эпика по id
        taskManager.getEpicById(epic.getId());
        taskManager.getEpicById(epic2.getId());

        //обновление задачи
        Task task3 = new Task(task.getId(), "Обновленная задачи 1", "Обновление описания задачи 1", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task3);

        //обновление эпика
        Epic epic3 = new Epic(epic.getId(), "Обновленная эпика 1", "Обновление описания эпика 1", TaskStatus.IN_PROGRESS, epic.getSubTasks());
        taskManager.updateEpic(epic3);

        //обновление подзадачи
        SubTask subTask4 = new SubTask(subTask.getId(), "Обновленная подзадача 1", "Обновление описания подзадачи 1", TaskStatus.IN_PROGRESS, subTask.getEpicId());
        taskManager.updateSubTask(subTask4);

        printAllTasks(taskManager);

        //получение всех задач
        taskManager.getTasks();

        //получение всех подзадач
        taskManager.getSubTasks();

        //получение всех эпиков
        taskManager.getEpics();



//        //удаление задачи по ид
//        taskManager.deleteTaskById(task2.getId());
//
//        //удаление эпика по ид
//        taskManager.deleteEpicById(epic2.getId());
//
//        //удаление подзадачи по ид
//        taskManager.deleteSubTaskById(subTask2.getId());

        //удаление всех задач
        taskManager.removeAllTask();

        //удаление всех эпиков
        taskManager.removeAllEpic();

        //удаление всех подзадач
        taskManager.removeAllSubTask();

        System.out.println("Проверка списков после удаления");
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);
            for (Task task : manager.getListSubTaskByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}