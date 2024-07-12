package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createSubTask(SubTask subTask);

    void createEpic(Epic epic);

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void removeAllTask();

    void removeAllSubTask();

    void removeAllEpic();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    List<SubTask> getListSubTaskByEpicId(int epicId);

    void deleteTaskById(int id);

    void deleteSubTaskById(int id);

    void deleteEpicById(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void checkEpicStatus(int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
