package com.yandex.add.service;

import com.yandex.add.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();
    private final static int HISTORY_LENGTH = 10;


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        System.out.println("added to history task :" + task.getIdNum());
        if (history.size() >= HISTORY_LENGTH) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

}
