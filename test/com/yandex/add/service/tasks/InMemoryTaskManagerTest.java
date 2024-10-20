package com.yandex.add.service.tasks;

import com.yandex.add.service.history.InMemoryHistoryManager;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private InMemoryTaskManager memoryTaskManager;

    @Override
    protected InMemoryTaskManager createManager() {
        return memoryTaskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}
