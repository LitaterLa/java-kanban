package com.yandex.add.model;

public class Subtask  extends Task {
private final int epicId;

    public Subtask(String title, String description, int idNum,int epicId) {
        super(title, description, idNum);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
