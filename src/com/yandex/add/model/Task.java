package com.yandex.add.model;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int idNum;
    private TaskStatus taskStatus;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.taskStatus = TaskStatus.NEW;
    }

    public Task(String title, String description, int idNum) {
        this.title = title;
        this.description = description;
        this.idNum = idNum;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIdNum(int id) {
        this.idNum = id;
    }

    public String getTitle() {
        return title;
    }

    public int getIdNum() {
        return idNum;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return idNum == task.idNum && Objects.equals(getTitle(), task.getTitle()) && Objects.equals(getDescription(), task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNum);
    }

    @Override
    public String toString() {
        return "com.yandex.add.model.Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", idNum=" + idNum +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
