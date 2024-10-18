package com.yandex.add.service;

import com.yandex.add.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File templateFile;

    @BeforeEach
    void init() throws IOException {
        templateFile = File.createTempFile("test1", ".csv");
        manager = new FileBackedTaskManager(templateFile);
    }

    @Test
    void shouldLoadFromFile() throws IOException {
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(templateFile);
        assertEquals(0, newManager.getTasks().size());
        assertEquals(0, newManager.getEpics().size());
        assertEquals(0, newManager.getSubtasks().size());
    }


    @Test
    public void shouldSaveAndLoadMultipleTasks() throws IOException {
        Task task1 = new Task("Task1", "desc1");
        Task task2 = new Task("task2", "desc2");
        manager.createTask(task1);
        manager.createTask(task2);
        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(templateFile);
        assertEquals("Task1", newManager.getTaskById(task1.getIdNum()).getTitle());
        assertEquals("task2", newManager.getTaskById(task2.getIdNum()).getTitle());
    }
}