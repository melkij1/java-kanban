package model;

import enums.TaskStatus;

import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status = TaskStatus.NEW;


    public Task (String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task (String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task (int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public void setId (int id) {
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

    public void setStatus (TaskStatus status) {
        this.status = status;
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
