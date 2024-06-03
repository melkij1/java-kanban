package model;

import enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTasks = new ArrayList<Integer>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, String description, TaskStatus  status, List<Integer> subTask) {
        super(id, title, description, status);
        this.subTasks = subTask;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "подзадачи=" + subTasks +
                '}';
    }
}
