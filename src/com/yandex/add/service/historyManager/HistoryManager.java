package com.yandex.add.service.historyManager;

import com.yandex.add.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);// помечает задачи как просмотренные

    void remove(int id);

    List<Task> getHistory();
}
