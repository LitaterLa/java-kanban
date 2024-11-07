package com.yandex.add.service.taskManager;

import com.yandex.add.exceptions.ValidationException;
import com.yandex.add.model.Epic;
import com.yandex.add.model.Status;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.Managers;
import com.yandex.add.service.history.HistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest {
    private InMemoryTaskManager manager;
    private HistoryManager historyManager;
    private Task task1, task2;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
        manager = new InMemoryTaskManager(historyManager);
        task1 = new Task("task1", "task1");
        manager.createTask(task1);
        task2 = new Task("task2", "task2");
        manager.createTask(task2);
        epic = new Epic("epic1", "epic1");
        manager.createEpic(epic);
        subtask = new Subtask("subtask1", "subtask1", epic.getId());
        manager.createSubtask(subtask);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
    }

    @Test
    void shouldReturnSameHistoryOnNewTaskManager() {
        manager.getEpicByID(epic.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        Assertions.assertEquals(task1, history.get(0));
    }

    @Test
    void shouldGetEpicViaSubtask() {
        assertNotNull(manager.epics.get(subtask.getEpicId()));
    }

    @Test
    void shouldTestEpicStatus() {
        Subtask subtask2 = manager.createSubtask(new Subtask("subtask2", "subtask2", epic.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("subtask2", "subtask2", epic.getId()));
        assertTrue(epic.getStatus().equals(Status.NEW), "статус эпика при создании новый подзадач - NEW");

        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);
        assertTrue(epic.getStatus().equals(Status.IN_PROGRESS));

        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);
        assertTrue(epic.getStatus().equals(Status.DONE), "Обновление статуса эпика - DONE");

    }

    @Test
    void shouldDeleteTaskById() {
        manager.deleteTaskById(task1.getId());
        assertNull(manager.getTaskById(task1.getId()));
    }

    @Test
    void shouldDeleteEpicByIdAndItsSubtasks() {
        manager.deleteEpicById(epic.getId());
        assertNull(manager.getEpicByID(epic.getId()));
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldUpdateTask() {
        task1.setTitle("newTitle");
        manager.updateTask(task1);
        Task updated = manager.getTaskById(task1.getId());
        assertEquals(task1.getTitle(), updated.getTitle());
        assertEquals(task1.getDescription(), updated.getDescription());
        assertEquals(task1.getStatus(), updated.getStatus());
    }

    @Test
    void shouldReturnSubtasksForEpic() {
        List<Subtask> subtasks = manager.getSubtasksByEpic(epic.getId());
        assertEquals(1, subtasks.size());
    }

    @Test
    void shouldNotAddEpicAsSubtask() {
        Epic savedEpic = manager.getEpicByID(epic.getId());
        assertNotNull(savedEpic);
        savedEpic.setStatus(Status.DONE);
        assertEquals(Status.DONE, savedEpic.getStatus());
    }

    @Test
    void shouldAddAndFindDifferentTypesOfTasks() {
        assertNotNull(manager.getTaskById(task1.getId()));
        assertNotNull(manager.getEpicByID(epic.getId()));
        assertNotNull(manager.getSubtaskByID(subtask.getId()));

    }

    @Test
    void shouldHaveUniqueId() {
        assertNotEquals(task2.getId(), task1.getId());
    }

    @Test
    void shouldOverlapAndThrowException() {
        Task task11 = new Task(200, "Task 1", Status.IN_PROGRESS, "Description 1", LocalDateTime.of(2023, 10, 1, 10, 0), Duration.ofHours(2));
        manager.createTask(task11);
        Task task22 = new Task(201, "Task 2", Status.IN_PROGRESS, "Description 2", LocalDateTime.of(2023, 10, 1, 11, 0), Duration.ofHours(2));
        assertThrows(ValidationException.class, () -> manager.createTask(task22),
                "Исключение ValidationException, тк задачи пересекаются по времени");
    }
}
