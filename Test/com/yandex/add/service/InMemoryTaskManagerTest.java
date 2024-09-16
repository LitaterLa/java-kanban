package com.yandex.add.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private InMemoryTaskManager memoryTaskManager;
    private EmptyHistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = new EmptyHistoryManager();
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
        Subtask subtask = new Subtask("subtask", "subtask", 1000);
        Epic epic = new Epic("epic", "epic");

        memoryTaskManager.createEpic(epic);
        memoryTaskManager.createSubtask(subtask);
        memoryTaskManager.updateEpic(epic);
        memoryTaskManager.updateSubtask(subtask);
        assertNotEquals(epic.getDescription(), subtask.getDescription());
        assertNotEquals(epic.getTitle(), subtask.getTitle());
        //плохо понимаю что должен делать этот тест
    }

    @Test
    public void shouldNotAddSubtaskAsItsEpic(){
        Subtask subtask = memoryTaskManager.createSubtask(new Subtask("title", "desc", 50));
        Epic epic = memoryTaskManager.createEpic(new Epic("epic", "epicD"));

        memoryTaskManager.updateSubtask(subtask);
        assertNotEquals(epic.getDescription(), subtask.getDescription());
        assertNotEquals(epic.getTitle(), subtask.getTitle());
        //плохо понимаю что должны делать последние два теста, пыталась идти по рекомендации наставника. немного знакома с исключ, нашла такое искл
    /*
    @Test (expected = IllegalArgumentException.class)
    public void shouldNotAddEpicAsSubtask(){
    Subtask subtask = memoryTaskManager.createSubtask(new Subtask("title", "desc", 50));
    Epic epic = memoryTaskManager.createEpic(new Epic("epic", "epicD", subtask.getIdNum()));

    epic.addSubtask(subtask); получу искл
    }

       @Test(expected = IllegalArgumentException.class)
   public void shouldNotAddSubtaskAsItsEpic() {
       Subtask subtask = new Subtask(1, "Subtask1", "Description1", Status.NEW, 1);
       subtask.setEpicId(subtask.getId());
   }
     */

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
        EmptyHistoryManager historyManager = new EmptyHistoryManager();
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(historyManager);
        assertEqualTaskManagers(memoryTaskManager, inMemoryTaskManager, "should be equal");
    }
    private static void assertEqualTaskManagers(TaskManager expected, TaskManager actual, String message){
        assertEquals(expected.getTasks(), actual.getTasks());
    }

}

class EmptyHistoryManager implements HistoryManager {
    @Override
    public void add(Task task) {

    }

    @Override
    public List<Task> getHistory() {
        return Collections.emptyList();
    }
}
