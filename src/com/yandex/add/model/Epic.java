package com.yandex.add.model;

public class Epic extends Task {
    public Epic(String title, String description) {
        super(title, description);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}
