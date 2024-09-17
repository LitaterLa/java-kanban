package com.yandex.add.service;

import com.yandex.add.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    Task task = new Task("title", "description");
    final List<Task> history = historyManager.getHistory();

    @Test
    void shouldBeLessThan10InSizeHistory() {
        for (int i = 0; i < 15; i++) {
            historyManager.add(new Task("title", "d"));
        }
        assertEquals(10, historyManager.getHistory().size());
    }

    @Test
    void shouldAddImmutableTask() {
        historyManager.add(task);
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
        assertEquals(task.getTitle(), history.getFirst().getTitle());
        assertEquals(task.getDescription(), history.getFirst().getDescription());
        assertEquals(task.getIdNum(), history.getFirst().getIdNum());
        assertEquals(task.getTaskStatus(), history.getFirst().getTaskStatus());
    }

    @Test
    void getHistory() {
        historyManager.add(task);
        List<Task> history2 = historyManager.getHistory();
        assertEquals(history.size(), history2.size());
        assertEquals(history.getFirst(), history2.getFirst());
    }
}
