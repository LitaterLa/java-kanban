package com.yandex.add.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private InMemoryTaskManager memoryTaskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        memoryTaskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void shouldDeleteTaskById(){
        Task task = new Task("task", "description");
        memoryTaskManager.createTask(task);
        memoryTaskManager.deleteTaskById(task.getIdNum());
        assertNull(memoryTaskManager.getTaskById(task.getIdNum()));
    }

    @Test
    void shouldDeleteEpicByIdAndItsSubtasks(){
        Epic epic = new Epic("epic","epic");
        Subtask subtask1 = new Subtask("subtask1", "decr", epic.getIdNum());
        Subtask subtask2 = new Subtask("subtask2", "decr", epic.getIdNum());
        memoryTaskManager.createEpic(epic);
        memoryTaskManager.createSubtask(subtask1);
        memoryTaskManager.createSubtask(subtask2);

        memoryTaskManager.deleteEpicById(epic.getIdNum());
        assertNull(memoryTaskManager.getEpicByID(epic.getIdNum()));
        assertEquals(0, memoryTaskManager.getSubtasks().size());
    }

    @Test
    void shouldUpdateTask(){
        Task task = new Task("task", "description");
        memoryTaskManager.createTask(task);
        task.setTitle("newTitle");
        memoryTaskManager.updateTask(task);
        Task updated = memoryTaskManager.getTaskById(task.getIdNum());
        assertEquals(task.getTitle(), updated.getTitle());
        assertEquals(task.getDescription(), updated.getDescription());
        assertEquals(task.getTaskStatus(), updated.getTaskStatus());
    }

    @Test
    void getSubtasksByEpic_ShouldReturnSubtasksForEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description 1");
        memoryTaskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", epic.getIdNum());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", epic.getIdNum());
        memoryTaskManager.createSubtask(subtask1);
        memoryTaskManager.createSubtask(subtask2);

        List<Subtask> subtasks = memoryTaskManager.getSubtasksByEpic(epic.getIdNum());

        assertEquals(2, subtasks.size());
    }

    @Test
    void shouldNotAddEpicAsSubtask() {
        Epic epic = new Epic("epic", "epic");
        memoryTaskManager.createEpic(epic);
        Epic savedEpic = memoryTaskManager.getEpicByID(epic.getIdNum());
        assertNotNull(savedEpic);

        Subtask subtask = new Subtask("subtask", "subtask", epic.getIdNum());
        subtask.setTaskStatus(TaskStatus.DONE);
        memoryTaskManager.createSubtask(subtask);
        assertEquals(TaskStatus.DONE, savedEpic.getTaskStatus());
    }

    @Test
    void shouldAddAndFindDifferentTypesOfTasks(){
        Task task = memoryTaskManager.createTask(new Task("task", "task"));
        Epic epic = memoryTaskManager.createEpic(new Epic("epic", "epic"));
        Subtask subtask = memoryTaskManager.createSubtask(new Subtask("subtask", "subtask",epic.getIdNum()));
        memoryTaskManager.createTask(task);
        memoryTaskManager.createEpic(epic);
        memoryTaskManager.createSubtask(subtask);

        assertNotNull(memoryTaskManager.getTaskById(task.getIdNum()));
        assertNotNull(memoryTaskManager.getEpicByID(epic.getIdNum()));
        assertNotNull(memoryTaskManager.getSubtaskByID(subtask.getEpicId()));

    }

    @Test
    void shouldHaveUniqueId(){
        Task task = new Task("task", "task");
        Task task2 = new Task("task", "task");
        memoryTaskManager.createTask(task);
        memoryTaskManager.createTask(task2);
        assertNotEquals(task2.getIdNum(), task.getIdNum());
    }
    @Test
    void shouldBeImmutableTask(){
        Task originalTask = new Task("Task1", "Description1");
        String name = originalTask.getTitle();
        String description = originalTask.getDescription();
        TaskStatus ts = originalTask.getTaskStatus();
        memoryTaskManager.createTask(originalTask);
        assertEquals(originalTask.getTitle(), name);
        assertEquals(originalTask.getDescription(), description);
        assertEquals(originalTask.getTaskStatus(), ts);
    }
    @Test
    public void taskManagers() {
        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(historyManager);
        assertEqualTaskManagers(memoryTaskManager, inMemoryTaskManager, "should be equal");
    }
    private static void assertEqualTaskManagers(TaskManager expected, TaskManager actual, String message){
        assertEquals(expected.getTasks(), actual.getTasks());
    }

}
