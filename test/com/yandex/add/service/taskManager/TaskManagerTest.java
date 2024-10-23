package com.yandex.add.service.taskManager;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    private T manager;
    private Task task1;
    private Task task2;
    private Subtask subtask;
    private Epic epic;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        manager = createManager();
        task1 = manager.createTask(new Task("task1", "description"));
        task2 = manager.createTask(new Task("task2", "description2"));
        epic = manager.createEpic(new Epic("epic", "epic"));
        subtask = manager.createSubtask(new Subtask("subtask", "subtask", epic.getIdNum()));
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
    void getSubtasksByEpic_ShouldReturnSubtasksForEpic() {
        List<Subtask> subtasks = manager.getSubtasksByEpic(epic.getIdNum());
        assertEquals(1, subtasks.size());
    }

    @Test
    void shouldNotAddEpicAsSubtask() throws IOException {
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

}
