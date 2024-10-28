package com.yandex.add.service.taskManager;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;
import com.yandex.add.service.history.HistoryManager;
import com.yandex.add.service.history.InMemoryHistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected static final Map<Integer, Task> tasks = new HashMap<>();
    protected static final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected static final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Epic, List<Subtask>> epicsWithSubtasks = new HashMap<>();
    private int idCounter = 0;
    protected static int seq = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManager() {
        this.historyManager = new InMemoryHistoryManager();
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
        subtask.setIdNum(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epicsWithSubtasks.get(epic).add(subtask);
            subtasks.put(subtask.getIdNum(), subtask);
            setEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAll() {
        deleteAllEpics();
        deleteAllTasks();
        deleteAllTasks();
        deleteAllEpics();
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getIdNum());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getIdNum());
        }
        subtasks.clear();
        epicsWithSubtasks.values().clear();

        for (Epic epic : epics.values()) {
            setEpicStatus(epic);
        }
        for (Epic epic : epics.values()) {
            setEpicStatus(epic);
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getIdNum());
        }
        subtasks.clear();
        epicsWithSubtasks.values().clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getIdNum());
        }
        epics.clear();
        epicsWithSubtasks.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteTaskById(int id) {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getIdNum());
        }
        if (tasks.get(id) != null) {
            tasks.remove(id);
            historyManager.remove(id);
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

        List<Subtask> removed = epicsWithSubtasks.remove(epicToRemove);
        if (removed != null) {
            for (Subtask subtask : removed) {
                subtasks.remove(subtask.getIdNum());
                historyManager.remove(subtask.getIdNum());
            }
        }
        historyManager.remove(id);
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
        historyManager.remove(id);
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
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    private int generateId() {
        ++seq;
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




