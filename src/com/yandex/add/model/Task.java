package com.yandex.add.model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private Status status;
    private String description;

    private LocalDateTime startTime;
    private Duration duration;
    private LocalDateTime endTime;

    public Task(String title, String description) {
        this.title = title;
        this.status = Status.NEW;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ZERO;
        this.endTime = calculateEndTime();
    }

    public Task(int id, String title, Status status, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = calculateEndTime();
    }

    public LocalDateTime calculateEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Integer getEpicId() {
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        return id == task.id && Objects.equals(getTitle(), task.getTitle()) && Objects.equals(getDescription(), task.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "com.yandex.add.model.Task{" + "title='" + title + '\'' + ", description='" + description + '\'' + ", idNum=" + id + ", taskStatus=" + status + '}';
    }

}
