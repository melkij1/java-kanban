package model;

import enums.TaskStatus;
import enums.TaskType;
import util.ServiceConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status = TaskStatus.NEW;
    private LocalDateTime startTime;
    private long duration;

    final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(ServiceConstants.DATE_FORMAT);

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, String description, TaskStatus status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime()  {
        if (startTime != null) {
          return startTime.plusMinutes(duration);
        }
        return null;
    }

    public String getStartTimeString() {
        if (startTime != null) {
            return startTime.format(DATE_FORMATTER);
        }
        return null;
    }

    public String getEndTimeString() {
        if (startTime != null) {
            return getEndTime().format(DATE_FORMATTER);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task that = (Task) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(description, that.description) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        return "Задача{" +
                "id=" + id +
                ", название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", статус=" + status +
                '}';
    }
}
