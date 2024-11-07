package com.yandex.add.service.history;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Status;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
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
        task1 = new Task(300, "title1", Status.NEW, "description", LocalDateTime.now(), Duration.ofMinutes(15));
        task2 = new Task(301, "title2", Status.NEW, "description", LocalDateTime.now(), Duration.ofMinutes(15));
        task3 = new Task(302, "title3", Status.NEW, "description", LocalDateTime.now(), Duration.ofMinutes(15));
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @Test
    void testRemoveFirst() {
        historyManager.remove(task1.getId());
        assertEquals(historyManager.getHistory(), List.of(task2, task3));
    }

    @Test
    void shouldAddImmutableTask() {
        history.addAll(historyManager.getHistory());
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая.");
        assertEquals(task1.getTitle(), history.get(0).getTitle());
        assertEquals(task1.getDescription(), history.getFirst().getDescription());
        assertEquals(task1.getId(), history.getFirst().getId());
        assertEquals(task1.getStatus(), history.getFirst().getStatus());
    }

    @Test
    void shouldNotSaveOldID() {
        historyManager.remove(task1.getId());
        Task newTask = new Task("new", "newer");
        assertNotEquals(task1.getId(), newTask.getId());
    }

    @Test
    void shouldNotHoldDisusedSubtaskId() {
        Epic epic = new Epic(5, "epic", Status.NEW, "description", LocalDateTime.now(), Duration.ofMinutes(15));
        Subtask subtask1 = new Subtask("subtask", "subtask", 5);
        Subtask subtask2 = new Subtask("subtask", "subtask", 5);
        historyManager.add(epic);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.remove(subtask1.getId());
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

    @Test
    void shouldNotHoldDuplicates() {
        List<Task> history1 = historyManager.getHistory();
        historyManager.add(task1);
        List<Task> history2 = historyManager.getHistory();
        assertEquals(history1.size(), history2.size());
    }

    @Test
    void shouldDeleteTasksStartMiddleEnd() {
        Task task = new Task(304, "title5", Status.NEW, "description", LocalDateTime.now(), Duration.ofMinutes(15));
        Epic epic = new Epic(305, "title6", Status.NEW, "description", LocalDateTime.now(), Duration.ofMinutes(15));
        Subtask subtask = new Subtask(306, "title7", Status.NEW, "description", 305, LocalDateTime.now(), Duration.ofMinutes(15));
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> h = historyManager.getHistory();
        historyManager.remove(task1.getId());
        List<Task> h3 = historyManager.getHistory();
        Task t = h3.getFirst();

        assertEquals(historyManager.getHistorySize(), (h.size() - 1), "удаление первого объекта");
        assertEquals(task2.getTitle(), t.getTitle(), "теперь вторая задача - HEAD");

        historyManager.remove(subtask.getId());
        List<Task> h4 = historyManager.getHistory();
        Task e = h4.getLast();
        assertEquals(epic.getTitle(), e.getTitle(), "теперь epic - TAIL");

        historyManager.remove(task3.getId());
        List<Task> h5 = historyManager.getHistory();
        assertNotEquals(h5.get(2), h4.get(2), "проверка удаления из середины");
    }

    @Test
    void shouldBeVoidHistory() {
        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());

        assertTrue(historyManager.getHistory().isEmpty());
    }
}
