package com.yandex.add.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Subtask> subtasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Epic, List<Subtask>> epicsWithSubtasks = new HashMap<>();
    private int idCounter = 1;
    HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
    }

    @Override
    public Task createTask(Task task) {
        task.setIdNum(generateId());
        tasks.put(task.getIdNum(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setIdNum(generateId());
        setEpicStatus(epic);
        epics.put(epic.getIdNum(), epic);
        epicsWithSubtasks.put(epic, new ArrayList<>());
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
       subtask.setIdNum(subtask.getEpicId());
       //здесь внесла изменен, было subtask.setIdNum(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epicsWithSubtasks.get(epic).add(subtask);
            subtasks.put(subtask.getIdNum(), subtask);
            setEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public void deleteAll() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
        epicsWithSubtasks.clear();
        System.out.println("Deleted!");
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Deleted!");
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        epicsWithSubtasks.values().clear();
        for (Epic epic : epics.values()) {
            setEpicStatus(epic);
        }
        System.out.println("Deleted!");
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        epicsWithSubtasks.clear();
        subtasks.clear();
        System.out.println("Deleted!");
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.get(id) != null) {
            tasks.remove(id);
        } else {
            System.out.println("Id not found.");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove == null) {
            System.out.println("Not found");
        }

        List<Subtask> subtasks = epicsWithSubtasks.remove(epicToRemove);
        if (subtasks != null) {
            for (Subtask subtask : subtasks) {
                this.subtasks.remove(subtask.getIdNum());
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        final int epicId = subtask.getEpicId();
        final Epic epic = epics.get(epicId);
        epicsWithSubtasks.get(epic).remove(subtask);
        setEpicStatus(epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getIdNum(), task);

    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getIdNum());
        oldEpic.setDescription(epic.getDescription());
        oldEpic.setTitle(epic.getTitle());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Subtask oldSubtask = subtasks.get(subtask.getIdNum());
        if (oldSubtask == null) {
            return;
        }
        oldSubtask.setDescription(subtask.getDescription());
        oldSubtask.setTitle(subtask.getTitle());
        oldSubtask.setTaskStatus(subtask.getTaskStatus());
        setEpicStatus(epics.get(oldSubtask.getEpicId()));
    }


    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksByEpic(int epicID) {
        Epic epic = epics.get(epicID);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Subtask> subtasks = epicsWithSubtasks.get(epic);
        if (subtasks == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(subtasks);
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Task getEpicByID(int id) {
        return epics.get(id);
    }

    @Override
    public Task getSubtaskByID(int id) {
        return subtasks.get(id);
    }

    private int generateId() {
        return ++idCounter;
    }

    private void setEpicStatus(Epic epic) {
        List<Subtask> tempSubtasks = epicsWithSubtasks.get(epic);
        if (tempSubtasks == null || tempSubtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        boolean isNew = false;
        boolean isDone = false;

        for (Subtask tempSubtask : tempSubtasks) {
            TaskStatus status = tempSubtask.getTaskStatus();
            if (status.equals(TaskStatus.DONE)) {
                isDone = true;
            } else if (status.equals(TaskStatus.NEW)) {
                isNew = true;
            } else {
                epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                return;
            }
        }
        if (isDone && isNew) {
            epic.setTaskStatus((TaskStatus.IN_PROGRESS));
        } else if (isDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.NEW);
        }

    }

}




