package com.yandex.add.service;

import com.yandex.add.model.Task;
import com.yandex.add.service.historyManager.HistoryManager;
import com.yandex.add.service.taskManager.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    public void shouldNotBeNullTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager should not be null");
    }

    @Test
    public void shouldNotBeNullHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager should not be null");
    }

    @Test
    void shouldImplementMethodsEfficiently() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();

        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "HistoryManager должен содержать 1 задачу в истории.");
        assertEquals(task, history.get(0), "История должна содержать добавленную задачу.");


        // либо можно проверить на принадлежность к классу, не поняла, что требуется по тз
        //assertTrue(taskManager instanceof InMemoryTaskManager, "TaskManager should be an instance of InMemoryTaskManager");
    }
}


