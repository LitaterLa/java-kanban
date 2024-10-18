package com.yandex.add.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;

import java.util.List;

public interface TaskManager {

    Task createTask(Task task) throws FileBackedTaskManager.ManagerSaveException;

    Epic createEpic(Epic epic) throws FileBackedTaskManager.ManagerSaveException;

    Subtask createSubtask(Subtask subtask) throws FileBackedTaskManager.ManagerSaveException;

    List<Task> getHistory();

    void deleteAll();

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    void deleteTaskById(int id) throws FileBackedTaskManager.ManagerSaveException;

    void deleteEpicById(int id) throws FileBackedTaskManager.ManagerSaveException;

    void deleteSubtaskById(int id) throws FileBackedTaskManager.ManagerSaveException;

    void updateTask(Task task) throws FileBackedTaskManager.ManagerSaveException;

    void updateEpic(Epic epic) throws FileBackedTaskManager.ManagerSaveException;

    void updateSubtask(Subtask subtask) throws FileBackedTaskManager.ManagerSaveException;

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpic(int epicID);

    Task getTaskById(int id);

    Epic getEpicByID(int id);

    Subtask getSubtaskByID(int id);
}
