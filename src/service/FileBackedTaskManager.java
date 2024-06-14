package service;

import enums.TaskStatus;
import enums.TaskType;
import exceptions.ManagersSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import service.impl.InMemoryTaskManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File FILE;

    private static final String FIRST_LINE_NAME = "id,type,name,status,description,epic_id";

    public FileBackedTaskManager(File file) {
        this.FILE  = file;
    }

    //загрузка задач из файла при запуске приложения
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try(BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8)))  {
            List<String> lines  = reader.lines().collect(Collectors.toList());
//            System.out.println(lines);
            for (int i = 1; i < lines.size(); i++)  {
                if(lines.get(i).isEmpty()){
                    break;
                }
                String[] line = lines.get(i).split(",");
                Task task = fromString(line);
                switch (task.getTaskType()) {
                    case TASK -> manager.createTask(task);
                    case EPIC -> manager.createEpic((Epic) task);
                    case SUBTASK -> manager.createSubTask((SubTask) task);
                }
            }
        } catch (IOException ex)   {
            ex.printStackTrace();
        }

        return manager;
    }

    //создание задачи из строки
    private static Task fromString(String[] line)  {
        int id = Integer.parseInt(line[0]);
        TaskType type= TaskType.valueOf(line[1]);
        String name = line[2];
        TaskStatus status= TaskStatus.valueOf(line[3]);
        String description = line[4];
        switch (type) {
            case TASK:
                return new Task(id,name,description,status);
            case EPIC:
                return new Epic(id,name,description,status);
            case SUBTASK:
                int epicId= Integer.parseInt(line[5]);
                return new SubTask(id,name,description,status, epicId);
        }
        return null;
    }


    public void save(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, StandardCharsets.UTF_8)))  {
            writer.write(FIRST_LINE_NAME);
            writer.newLine();
            addAllTasksToFile(writer);
            writer.newLine();
        }catch (IOException e){
            throw new ManagersSaveException();
        }
    }

    private void addAllTasksToFile(BufferedWriter writer) throws IOException {
        for (Task task : getTasks()) {
            writer.write(toString(task));
            writer.newLine();
        }

        for (Epic epic : getEpics()) {
            writer.write(toString(epic));
            writer.newLine();
        }

        for (SubTask subTask : getSubTasks()) {
            writer.write(toString(subTask));
            writer.newLine();
        }
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription();
    }

    private String toString(Epic epic) {
        return epic.getId() + "," + epic.getTaskType() + "," + epic.getTitle() + "," + epic.getStatus() + "," + epic.getDescription();
    }

    private String toString(SubTask subTask)  {
        return subTask.getId() + "," + subTask.getTaskType() + "," + subTask.getTitle() + "," + subTask.getStatus() + "," + subTask.getDescription() + "," + subTask.getEpicId();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subTask)  {
        super.createSubTask(subTask);
        save();
    }


}
