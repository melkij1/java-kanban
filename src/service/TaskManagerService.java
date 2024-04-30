package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;

public interface TaskManagerService {

    void createTask(Task task);

    void createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<Epic> getEpics();

    void removeAllTask();

    void removeAllSubTask();

    void removeAllEpic();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    ArrayList<SubTask> getListSubTaskByEpicId(int epicId);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void checkEpicStatus(int id);
}
