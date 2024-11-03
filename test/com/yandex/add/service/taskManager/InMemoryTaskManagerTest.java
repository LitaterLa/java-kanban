package com.yandex.add.service.taskManager;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;
import com.yandex.add.service.Managers;
import com.yandex.add.service.history.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
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
        subtask = new Subtask("subtask1", "subtask1", epic.getIdNum());
        manager.createSubtask(subtask);
        manager.getTaskById(task1.getIdNum());
        manager.getTaskById(task2.getIdNum());
    }

    @Test
    void shouldBeImmutableTask() {
        Task originalTask = new Task("Task1", "Description1");
        String name = originalTask.getTitle();
        String description = originalTask.getDescription();
        TaskStatus ts = originalTask.getTaskStatus();
        manager.createTask(originalTask);
        assertEquals(originalTask.getTitle(), name);
        assertEquals(originalTask.getDescription(), description);
        assertEquals(originalTask.getTaskStatus(), ts);
    }

    @Test
    void shouldReturnSameHistoryOnNewTaskManager() {
        manager.getEpicByID(epic.getIdNum());

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void shouldDeleteTaskById() {
        manager.deleteTaskById(task1.getIdNum());
        assertNull(manager.getTaskById(task1.getIdNum()));
    }

    @Test
    void shouldDeleteEpicByIdAndItsSubtasks() {
        manager.deleteEpicById(epic.getIdNum());
        assertNull(manager.getEpicByID(epic.getIdNum()));
        assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    void shouldUpdateTask() {
        task1.setTitle("newTitle");
        manager.updateTask(task1);
        Task updated = manager.getTaskById(task1.getIdNum());
        assertEquals(task1.getTitle(), updated.getTitle());
        assertEquals(task1.getDescription(), updated.getDescription());
        assertEquals(task1.getTaskStatus(), updated.getTaskStatus());
    }

    @Test
    void getSubtasksByEpicShouldReturnSubtasksForEpic() {
        List<Subtask> subtasks = manager.getSubtasksByEpic(epic.getIdNum());
        assertEquals(1, subtasks.size());
    }

    @Test
    void shouldNotAddEpicAsSubtask() {
        Epic savedEpic = manager.getEpicByID(epic.getIdNum());
        assertNotNull(savedEpic);
        savedEpic.setTaskStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, savedEpic.getTaskStatus());
    }

    @Test
    void shouldAddAndFindDifferentTypesOfTasks() {
        assertNotNull(manager.getTaskById(task1.getIdNum()));
        assertNotNull(manager.getEpicByID(epic.getIdNum()));
        assertNotNull(manager.getSubtaskByID(subtask.getIdNum()));

    }

    @Test
    void shouldHaveUniqueId() {
        assertNotEquals(task2.getIdNum(), task1.getIdNum());
    }


}