package com.yandex.add.service.tasks;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private FileBackedTaskManager manager;
    private Task taskOne;
    private Task taskTwo;
    private Task taskThree;
    private Epic epic;
    private File templateFile;

    {
        try {
            templateFile = File.createTempFile("test1", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void before() {
        manager = createManager();
        taskOne = new Task("Task1", "desc1");
        taskTwo = new Task("task2", "desc2");
        taskThree = new Task("task3", "task3");
        epic = new Epic("epic", "epic");
        manager.createTask(taskOne);
        manager.createTask(taskTwo);
        manager.createTask(taskThree);
        manager.createEpic(epic);
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(templateFile);
    }

    @Test
    void shouldLoadFromFile() throws IOException {
        FileBackedTaskManager newManager = manager.loadFromFile(templateFile);
        assertEquals(4, newManager.getTasks().size()); //почему считывает 4, а не 5??? (3 здесь, еще две в абстрактном классе)
        assertEquals(2, newManager.getEpics().size());
        assertEquals(1, newManager.getSubtasks().size());
        assertEquals(newManager.getTaskById(taskTwo.getIdNum()), manager.getTaskById(taskTwo.getIdNum()));
        assertEquals(newManager.getEpicByID(epic.getIdNum()), manager.getEpicByID(epic.getIdNum()));
    }

    @Test
    void shouldReturnLastAddedTaskId() throws IOException {
        int lastId = manager.seq;
        assertEquals(lastId, taskThree.getIdNum());
    }

    @Test
    public void shouldSaveAndLoadMultipleTasks() throws IOException {
        FileBackedTaskManager newManager = manager.loadFromFile(templateFile);
        assertEquals("Task1", newManager.getTaskById(taskOne.getIdNum()).getTitle());
        assertEquals("task2", newManager.getTaskById(taskTwo.getIdNum()).getTitle());
    }
}