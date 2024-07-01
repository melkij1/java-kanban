package model;

import enums.TaskStatus;
import enums.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subTasks = new ArrayList<Integer>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, String description, TaskStatus  status, LocalDateTime startTime, long duration, List<Integer> subTask) {
        super(id, title, description, status, startTime, duration);
        this.subTasks = subTask;
    }

    public Epic(int id, String title, String description, TaskStatus  status, LocalDateTime startTime, long duration) {
        super(id, title, description, status, startTime, duration);
    }

    public Epic(int id, String title, String description, TaskStatus  status, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(id, title, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String getEndTimeString() {
        if (endTime == null) {
            return "null";
        }
        return getEndTime().format(DATE_FORMATTER);
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
