package model;

import enums.TaskStatus;
import enums.TaskType;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, int epicId, LocalDateTime startTime, long duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(int id, String title, String description, TaskStatus status, int epicId, LocalDateTime startTime, long duration) {
        super(id, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
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
