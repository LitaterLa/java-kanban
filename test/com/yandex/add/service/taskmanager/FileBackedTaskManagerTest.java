package com.yandex.add.service.taskmanager;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest {

    private FileBackedTaskManager manager;
    private Task taskOne;
    private Task taskTwo;
    private Task taskThree;
    private Epic epic;
    private File templateFile;
    private Subtask subtask;

    {
        try {
            templateFile = File.createTempFile("test1", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileBackedTaskManager newManager;


    @BeforeEach
    void before() {
        manager = new FileBackedTaskManager(templateFile);
        taskOne = new Task("title1", "description");
        taskTwo = new Task("title2", "description");
        taskThree = new Task("title3", "description");
        epic = new Epic("epic", "description");
        manager.createTask(taskOne);
        manager.createTask(taskTwo);
        manager.createTask(taskThree);
        manager.createEpic(epic);
        subtask = new Subtask("title1", "description", epic.getId());
        manager.createSubtask(subtask);

    }

    @Test
    void shouldLoadFromFile() {
        newManager = manager.loadFromFile(templateFile);
        List<Task> tasks = newManager.getTasks();
        assertEquals(3, tasks.size());
        assertEquals(1, newManager.getEpics().size());
        assertEquals(1, newManager.getSubtasks().size());
        assertEquals(newManager.getTaskById(taskTwo.getId()), manager.getTaskById(taskTwo.getId()));
        assertEquals(newManager.getEpicByID(epic.getId()), manager.getEpicByID(epic.getId()));
    }

    @Test
    public void shouldSaveAndLoadMultipleTasks() {
        FileBackedTaskManager newManager = manager.loadFromFile(templateFile);
        assertEquals("title1", newManager.getTaskById(taskOne.getId()).getTitle());
        assertEquals("title2", newManager.getTaskById(taskTwo.getId()).getTitle());
    }
}


