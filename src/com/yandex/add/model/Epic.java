package com.yandex.add.model;

import com.yandex.add.exceptions.NotFoundException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(int id, String title, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, title, status, description, startTime, duration);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatusAndTime();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatusAndTime();
    }

    public void updateStatusAndTime() {
        Status status = Status.NEW;
        LocalDateTime start = LocalDateTime.MAX;
        Duration duration = Duration.ZERO;
        LocalDateTime end = LocalDateTime.MIN;

        boolean isInProgress = false;
        boolean isDone = false;

        System.out.println("Updating status and time for Epic with subtasks: " + subtasks.size() + " subtasks.");
        for (Subtask subtask : subtasks) {
            System.out.println("Processing subtask: " + subtask.getTitle() + " with status: " + subtask.getStatus());
            if (subtask.getStartTime().isBefore(start)) {
                start = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(end)) {
                end = subtask.getEndTime();
            }
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                isInProgress = true;
            } else if (subtask.getStatus() == Status.DONE) {
                isDone = true;
            }
            duration = duration.plus(subtask.getDuration());
        }
        if (isInProgress) {
            status = Status.IN_PROGRESS;
        } else if (isDone && !isInProgress) {
            status = Status.DONE;
        }
        System.out.println("Final start time: " + start);
        System.out.println("Final end time: " + end);
        System.out.println("Final duration: " + duration);
        System.out.println("Final status: " + status);

        this.setStartTime(start);
        this.setEndTime(end);
        this.setDuration(duration);
        this.setStatus(status);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasks.isEmpty()) {
            throw new NotFoundException("No subtasks found for epic with id: " + this.getId());
        }
        LocalDateTime minStartTime = subtasks.stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("No valid start times found in subtasks"));
        super.setStartTime(minStartTime);
        System.out.println("Epic start time: " + super.getStartTime());
        return super.getStartTime();
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtasks.isEmpty()) {
            throw new NotFoundException("No subtasks found for epic with id: " + this.getId());
        }
        LocalDateTime maxEndTime = subtasks.stream()
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("No valid end times found in subtasks"));

        super.setEndTime(maxEndTime);
        System.out.println("Epic ended time: " + getEndTime());
        return getEndTime();
    }

    @Override
    public Duration getDuration() {
        if (subtasks.isEmpty()) {
            throw new NotFoundException("No subtasks found for epic with id: " + this.getId());
        }
        return subtasks.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

    }


}
