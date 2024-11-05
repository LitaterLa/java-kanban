package com.yandex.add.service;


import com.yandex.add.service.history.HistoryManager;
import com.yandex.add.service.history.InMemoryHistoryManager;
import com.yandex.add.service.taskManager.InMemoryTaskManager;
import com.yandex.add.service.taskManager.TaskManager;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
