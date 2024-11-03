package com.yandex.add.service.history;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private Task task1, task2, task3;
    private List<Task> history;

    @BeforeEach
    public void setup() {
        history = new ArrayList<>();
        task1 = new Task("title1", "description", 300);
        task2 = new Task("title2", "description", 301);
        task3 = new Task("title3", "description", 302);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @Test
    void testRemoveFirst() {
        historyManager.remove(task1.getIdNum());
        assertEquals(historyManager.getHistory(), List.of(task2, task3));
    }

    @Test
    void shouldBeLessThan10InSizeHistory() {
        this.history.addAll(historyManager.getHistory());
        assertEquals(3, historyManager.getHistorySize());
    }

    @Test
    void shouldAddImmutableTask() {
        history.addAll(historyManager.getHistory());
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1.getTitle(), history.get(0).getTitle());
        assertEquals(task1.getDescription(), history.getFirst().getDescription());
        assertEquals(task1.getIdNum(), history.getFirst().getIdNum());
        assertEquals(task1.getTaskStatus(), history.getFirst().getTaskStatus());
    }

    @Test
    void shouldNotSaveOldID() {
        historyManager.remove(task1.getIdNum());
        Task newTask = new Task("new", "newer");
        assertNotEquals(task1.getIdNum(), newTask.getIdNum());
    }

    @Test
    void shouldNotHoldDisusedSubtaskId() {
        Epic epic = new Epic("epic", "epic", 5);
        Subtask subtask1 = new Subtask("subtask", "subtask", 5);
        Subtask subtask2 = new Subtask("subtask", "subtask", 5);
        historyManager.add(epic);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.remove(subtask1.getIdNum());
        List<Task> subtasks = historyManager.getHistory();
        assertEquals(4, subtasks.size());
    }

    @Test
    void shouldBeSameArrays() {
        List<Task> history1 = new ArrayList<>();
        history1.add(task1);
        history1.add(task2);
        history1.add(task3);
        List<Task> history2 = historyManager.getHistory();
        assertEquals(history1.size(), history2.size());
    }
}
