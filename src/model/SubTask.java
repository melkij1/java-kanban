package model;

import enums.TaskStatus;

import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask (String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask (int id, String title, String description, TaskStatus status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                super.toString() +
                ", id эпика='" + epicId + '}' + '\'';
    }
}
