package com.yandex.add.model;

public class Epic extends Task {
    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String epic1, int i) {
        super(title, epic1, i);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

}
