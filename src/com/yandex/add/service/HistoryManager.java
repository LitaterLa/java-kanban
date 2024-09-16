package com.yandex.add.service;

import com.yandex.add.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);// помечает задачи как просмотренные

    List<Task> getHistory();
}
