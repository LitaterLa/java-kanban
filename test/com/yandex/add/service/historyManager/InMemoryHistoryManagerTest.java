package com.yandex.add.service.historyManager;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.taskManager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
    private Task task1, task2, task3;
    private List<Task> history;

    @BeforeEach
    public void setup(){
        history = new ArrayList<>();
        task1 = new Task("title1", "description");
        task2 = new Task("title2", "description");
        task3 = new Task("title3", "description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
    }

    @Test
    void testRemoveFirst(){
        historyManager.remove(task1.getIdNum());
        assertEquals(historyManager.getHistory(), List.of(task2,task3));
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
        assertEquals(3,history.size(), "История не пустая.");
        assertEquals(task1.getTitle(), history.get(0).getTitle());
        assertEquals(task1.getDescription(), history.getFirst().getDescription());
        assertEquals(task1.getIdNum(), history.getFirst().getIdNum());
        assertEquals(task1.getTaskStatus(), history.getFirst().getTaskStatus());
    }

    @Test
    void shouldNotSaveOldID(){
    taskManager.deleteTaskById(task1.getIdNum());
    Task newTask = new Task("new", "newer");
    assertNotEquals(task1.getIdNum(), newTask.getIdNum());
    }

    @Test
    void shouldNotHoldDisusedSubtaskId(){
        Epic epic = new Epic("epic", "epic");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask", "subtask", epic.getIdNum());
        Subtask subtask2 = new Subtask("subtask", "subtask", epic.getIdNum());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1.getIdNum());
        historyManager.remove(subtask1.getIdNum());
        Subtask subtask = taskManager.getSubtaskByID(subtask1.getIdNum());
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());


    }
    @Test
    void shouldBeSameArrays(){
        List<Task> history1 = new ArrayList<>();
        history1.add(task1);
        history1.add(task2);
        history1.add(task3);
        taskManager.getTaskById(task1.getIdNum());
        taskManager.getTaskById(task2.getIdNum());
        taskManager.getTaskById(task3.getIdNum());
        List<Task> history2 = historyManager.getHistory();
        assertEquals(history1.size(), history2.size());
    }

    @Test
    void shouldFunctionManagerIfPropertiesChanges(){
        task1.setTitle("new title");
        task1.setDescription("new description");
        taskManager.updateTask(task1);
        List<Task> tasks = historyManager.getHistory();
        Task taskProb = new Task(null, null);
        for(Task task: tasks){
            if(task.equals(task1)){
               taskProb = task;
            }
        }
        assertEquals(task1, taskProb);
    }

}
