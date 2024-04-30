package model;

import enums.TaskStatus;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private ArrayList<Integer> tasks = new ArrayList<Integer>();

    public Epic(String title, String description){
        super(title, description);
    }

    public Epic(int id, String title, String description, TaskStatus  status, ArrayList<Integer> tasksIds) {
        super(id, title, description, status);
        this.tasks = tasksIds;
    }

    public ArrayList<Integer> getTasks() {
        return tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(tasks, epic.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tasks);
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "подзадачи=" + tasks +
                '}';
    }
}
