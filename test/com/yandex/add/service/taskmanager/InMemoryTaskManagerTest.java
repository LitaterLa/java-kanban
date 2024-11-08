package com.yandex.add.service.taskmanager;

import com.yandex.add.model.Status;
import com.yandex.add.model.Task;
import com.yandex.add.service.history.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    private InMemoryTaskManager manager = new InMemoryTaskManager(new InMemoryHistoryManager());

    @Test
    void shouldBeImmutableTask() {
        Task originalTask = new Task("Task1", "Description1");
        String name = originalTask.getTitle();
        String description = originalTask.getDescription();
        Status ts = originalTask.getStatus();
        manager.createTask(originalTask);
        assertEquals(originalTask.getTitle(), name);
        assertEquals(originalTask.getDescription(), description);
        assertEquals(originalTask.getStatus(), ts);
    }


}