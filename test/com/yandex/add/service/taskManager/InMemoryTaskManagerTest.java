package com.yandex.add.service.taskManager;

import com.yandex.add.service.history.HistoryManager;
import com.yandex.add.service.history.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private InMemoryTaskManager memoryTaskManager;
    private HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    protected InMemoryTaskManager createManager() {
        return memoryTaskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    public void taskManagers() {
        memoryTaskManager = createManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
        assertEqualTaskManagers(memoryTaskManager, taskManager, "should be equal");
    }

    private static void assertEqualTaskManagers(TaskManager expected, TaskManager actual, String message) {
        assertEquals(expected.getTasks(), actual.getTasks());
    }
}
