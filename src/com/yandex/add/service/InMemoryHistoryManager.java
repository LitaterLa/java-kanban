package com.yandex.add.service;

import com.yandex.add.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> history = new ArrayList<>(10);


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        System.out.println("added to history task :" + task.getIdNum());
        if (history.size() > 9) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
