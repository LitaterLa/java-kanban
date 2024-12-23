package com.yandex.add.service.taskmanager;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;

import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    List<Task> getHistory();

    void deleteAll();

    void deleteTasks();

    void deleteSubtasks();

    void deleteEpics();

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getSubtasksByEpic(int epicID);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskByID(int id);

    List<Task> getPrioritizedTasks();
}
