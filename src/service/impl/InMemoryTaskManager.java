package service.impl;

import enums.TaskStatus;
import model.Epic;
import model.SubTask;
import model.Task;
import service.HistoryManager;
import service.Manager;
import service.TaskManager;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager inMemoryHistoryManager = Manager.getDefaultHistory();

    //метод создание задачи
    @Override
    public void createTask(Task task) {
        task.setId(idCounter++);

        tasks.put(task.getId(), task);
    }

    //метод создание подзадачи
    @Override
    public void createSubTask(SubTask subTask) {
        subTask.setId(idCounter++);

        subTasks.put(subTask.getId(), subTask);

        //добавляем epic
        int epicId = subTask.getEpicId();

        ArrayList<Integer> tasksList = epics.get(epicId).getSubTasks();

        tasksList.add(subTask.getId());

        checkEpicStatus(epicId);
    }

    //метод создание эпика
    @Override
    public void createEpic(Epic epic) {
        epic.setId(idCounter++);

        epics.put(epic.getId(), epic);
    }

    //метод получения списка задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    //метод получения списка подзадач
    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    //метод получения списка эпиков
    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }


    //метод удаление всех задач
    @Override
    public void removeAllTask() {
        for (Task task : tasks.values()) {
            inMemoryHistoryManager.remove(task.getId());
        }
        tasks.clear();
    }

    //метод удаление всех подзадач
    @Override
    public void removeAllSubTask() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();

            checkEpicStatus(epic.getId());
        }

        for (Task task : subTasks.values()) {
            inMemoryHistoryManager.remove(task.getId());
        }

        subTasks.clear();
    }

    //метод удаление всех эпиков
    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            int epicId = epic.getId();

            ArrayList<Integer> tasksList = epics.get(epicId).getSubTasks();

            for (Integer taskId : tasksList) {
                tasks.remove(taskId);

                subTasks.remove(taskId);

                inMemoryHistoryManager.remove(taskId);
            }
        }
        for (Task task : epics.values()) {
            inMemoryHistoryManager.remove(task.getId());
        }
        epics.clear();
    }

    //метод получения задачи по id
    @Override
    public Task getTaskById(int id) {
        if (tasks.get(id) != null){
            inMemoryHistoryManager.addHistory(tasks.get(id));
        }
        return tasks.get(id);
    }

    //метод получения подзадачи по id
    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.get(id) != null){
            inMemoryHistoryManager.addHistory(subTasks.get(id));
        }
        return subTasks.get(id);
    }

    //метод получения эпика по id
    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null){
            inMemoryHistoryManager.addHistory(epics.get(id));
        }
        return epics.get(id);
    }

    //метод получения списка подзадачи по id эпика
    @Override
    public List<SubTask> getListSubTaskByEpicId(int epicId) {
        ArrayList<Integer> tasksList = epics.get(epicId).getSubTasks();
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        for (Integer taskId : tasksList) {
            subTasksList.add(subTasks.get(taskId));
        }
        return subTasksList;
    }

    //метод удаления задачи по id
    @Override
    public void deleteTaskById(int id) {
        inMemoryHistoryManager.remove(id);
        tasks.remove(id);
    }

    //метод удаления подзадачи по id
    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            Integer epicId = subTask.getEpicId();

            Epic epic = epics.get(epicId);
            if (epic != null) {
                ArrayList<Integer> tasksList = epic.getSubTasks();
                tasksList.removeIf(taskId -> taskId.equals(id));
                for (Integer taskId : tasksList) {
                    inMemoryHistoryManager.remove(taskId);
                }
                subTasks.remove(id);
                checkEpicStatus(epicId);
            } else {
                System.out.println("Эпик с id " + epicId + " не найден.");
            }
        } else {
            System.out.println("Подзадача с id " + id + " не найдена.");
        }
    }

    //метод удаления эпика по id
    @Override
    public void deleteEpicById(int id) {
        ArrayList<Integer> tasksList = epics.get(id).getSubTasks();
        for (Integer taskId : tasksList) {
            inMemoryHistoryManager.remove(taskId);

            tasks.remove(taskId);

            subTasks.remove(taskId);
        }
        epics.remove(id);
    }

    //метод обнолвения задачи
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //метод обнолвения подзадачи
    @Override
    public void updateSubTask(SubTask newSubTask) {
        subTasks.put(newSubTask.getId(), newSubTask);

        int epicId = subTasks.get(newSubTask.getId()).getEpicId();

        checkEpicStatus(epicId);
    }

    //метод обнолвения эпика
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    //метод проверки статуса подзадач в эпике
    @Override
    public void checkEpicStatus(int id) {
        int counterDone = 0;
        int counterNew = 0;

        ArrayList<Integer> subTasksList = epics.get(id).getSubTasks();

        for (Integer taskId : subTasksList) {
            if (subTasks.containsKey(taskId)){
                if (subTasks.get(taskId).getStatus().equals(TaskStatus.DONE)) {
                    counterDone++;
                } else if (subTasks.get(taskId).getStatus().equals(TaskStatus.NEW)) {
                    counterNew++;
                }
            }

        }

        if (subTasksList.size() == counterDone  || subTasksList.isEmpty()) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else if (subTasksList.size() == counterNew) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
