package service.impl;

import enums.TaskStatus;
import exceptions.TaskException;
import model.Epic;
import model.SubTask;
import model.Task;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static InMemoryTaskManager instance;
    private int idCounter = 0;

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    static final Comparator<Task> COMPARATOR = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected Set<Task> prioritizedTasks = new TreeSet<>(COMPARATOR);

    public InMemoryTaskManager() {
        // Приватный конструктор для предотвращения создания экземпляров
    }

    public static synchronized InMemoryTaskManager getInstance() {
        if (instance == null) {
            instance = new InMemoryTaskManager();
        }
        return instance;
    }


    //метод создание задачи
    @Override
    public void createTask(Task task) {
        validate(task);
        task.setId(idCounter++);

        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    //метод создание подзадачи
    @Override
    public void createSubTask(SubTask subTask) {
        validate(subTask);
        subTask.setId(idCounter++);

        subTasks.put(subTask.getId(), subTask);

        //добавляем epic
        int epicId = subTask.getEpicId();

        List<Integer> tasksList = epics.get(epicId).getSubTasks();

        if (!tasksList.contains(subTask.getId())) {
            tasksList.add(subTask.getId());
        }
        setEpicDateTime(epicId);
        checkEpicStatus(epicId);
        prioritizedTasks.add(subTask);
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
        for (Integer id : tasks.keySet()) {
            inMemoryHistoryManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
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

        for (Integer id : subTasks.keySet()) {
            inMemoryHistoryManager.remove(id);
        }

        subTasks.clear();
    }

    //метод удаление всех эпиков
    @Override
    public void removeAllEpic() {
        for (Epic epic : epics.values()) {
            int epicId = epic.getId();

            List<Integer> tasksList = epics.get(epicId).getSubTasks();

            for (Integer taskId : tasksList) {
                tasks.remove(taskId);

                subTasks.remove(taskId);

                inMemoryHistoryManager.remove(taskId);

                prioritizedTasks.remove(taskId);
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
        if (tasks.get(id) != null) {
            inMemoryHistoryManager.addHistory(tasks.get(id));
        }
        return tasks.get(id);
    }

    //метод получения подзадачи по id
    @Override
    public SubTask getSubTaskById(int id) {
        if (subTasks.get(id) != null) {
            inMemoryHistoryManager.addHistory(subTasks.get(id));
        }
        return subTasks.get(id);
    }

    //метод получения эпика по id
    @Override
    public Epic getEpicById(int id) {
        if (epics.get(id) != null) {
            inMemoryHistoryManager.addHistory(epics.get(id));
        }
        return epics.get(id);
    }

    //метод получения списка подзадачи по id эпика
    @Override
    public List<SubTask> getListSubTaskByEpicId(int epicId) {
        List<Integer> tasksList = epics.get(epicId).getSubTasks();
        List<SubTask> subTasksList = new ArrayList<>();
        for (Integer taskId : tasksList) {
            subTasksList.add(subTasks.get(taskId));
        }
        return subTasksList;
    }

    //метод удаления задачи по id
    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        inMemoryHistoryManager.remove(id);
        tasks.remove(id);
    }

    //метод удаления подзадачи по id
    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);

        if (subTask != null) {
            int epicId = subTask.getEpicId();
            prioritizedTasks.remove(subTasks.get(id));
            Epic epic = epics.get(epicId);
            if (epic != null) {
                List<Integer> tasksList = epic.getSubTasks();
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
        List<Integer> tasksList = epics.get(id).getSubTasks();
        for (Integer taskId : tasksList) {
            prioritizedTasks.remove(subTasks.get(taskId));
            inMemoryHistoryManager.remove(taskId);

            tasks.remove(taskId);

            subTasks.remove(taskId);
        }
        epics.remove(id);
    }

    //метод обнолвения задачи
    @Override
    public void updateTask(Task task) {
        validate(task);
        tasks.put(task.getId(), task);
    }

    //метод обнолвения подзадачи
    @Override
    public void updateSubTask(SubTask newSubTask) {
        validate(newSubTask);
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

        List<Integer> subTasksList = epics.get(id).getSubTasks();

        for (Integer taskId : subTasksList) {
            if (subTasks.containsKey(taskId)) {
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


    private void setEpicDateTime(int epicId) {
        List<Integer> subtasksList = epics.get(epicId).getSubTasks();

        if (subtasksList.isEmpty()) {
            epics.get(epicId).setDuration(0L);
            epics.get(epicId).setStartTime(null);
            epics.get(epicId).setEndTime(null);
        }
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        long epicDuration = 0L;
        for (Integer subTaskId : subtasksList) {
            SubTask subTask = subTasks.get(subTaskId);
            LocalDateTime subTaskStartTime = subTask.getStartTime();
            LocalDateTime subTaskEndTime = subTask.getEndTime();

            if (subTaskStartTime != null) {
                if (epicStartTime == null || subTaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subTaskStartTime;
                }
            }

            if (subTaskEndTime != null) {
                if (epicEndTime == null || subTaskEndTime.isBefore(epicEndTime)) {
                    epicEndTime = subTaskEndTime;
                }
            }
            epicDuration += subTasks.get(subTaskId).getDuration();
        }

        epics.get(epicId).setStartTime(epicStartTime);
        epics.get(epicId).setEndTime(epicEndTime);
        epics.get(epicId).setDuration(epicDuration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }


    private void validate(Task task) {
        List<Task> prioritizedTasks = getPrioritizedTasks();

        for (Task prioritizedTask : prioritizedTasks) {
            if (prioritizedTask.getStartTime() == null || prioritizedTask.getEndTime() == null) {
                return;
            }

            // Проверка на null перед сравнением ID
            if (task.getId() != null && Objects.equals(task.getId(), prioritizedTask.getId())) {
                continue;
            }

            if ((!task.getEndTime().isAfter(prioritizedTask.getStartTime())) ||
                    (!task.getStartTime().isBefore(prioritizedTask.getEndTime()))) {
                continue;
            }

            throw new TaskException("Время выполнения задачи пересекается с уже существующим временем. Выберите другую дату");
        }
    }
}
