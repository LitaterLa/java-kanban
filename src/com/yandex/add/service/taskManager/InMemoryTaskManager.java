package com.yandex.add.service.taskManager;

import com.yandex.add.exceptions.NotFoundException;
import com.yandex.add.exceptions.ValidationException;
import com.yandex.add.model.Epic;
import com.yandex.add.model.Status;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.Managers;
import com.yandex.add.service.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private int idCounter = 0;
    protected static int seq = 0;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        checkTaskTime(task);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        Epic epic = epics.get(subtask.getEpicId());
        checkTaskTime(subtask);
        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        calculateEpicStatus(epic);
        prioritizedTasks.add(subtask);
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
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeSubtask(subtask);
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            calculateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteTaskById(int id) {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
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
            throw new NotFoundException("Not found");
        }
        for (Subtask subtask : epicToRemove.getSubtasks()) {
            subtasks.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        prioritizedTasks.remove(epicToRemove);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        final Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            throw new NotFoundException("Подзадача не найдена");
        }
        final int epicId = subtask.getEpicId();
        final Epic epic = epics.get(epicId);
        epic.removeSubtask(subtask);
        prioritizedTasks.remove(subtask);
        calculateEpicStatus(epic);
        historyManager.remove(id);
    }

    @Override
    public void updateTask(Task task) {
        Task original = tasks.get(task.getId());
        if (original == null) {
            throw new NotFoundException("Task id=" + task.getId());
        }
        deleteOldAndAddNewTask(task, original);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic original = epics.get(epic.getId());
        List<Subtask> subtasks;
        if (original == null) {
            throw new NotFoundException("Эпик id+" + original.getId());
        }
        List<Subtask> epicSubtasks = epic.getSubtasks();
        if (epicSubtasks == null) {
            subtasks = new ArrayList<>();
        }
        epic.updateStatusAndTime();
        deleteOldAndAddNewTask(epic, original);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        final Subtask original = subtasks.get(subtask.getId());
        if (original == null) {
            throw new NotFoundException("Subtask id+" + original.getId());
        }
        deleteOldAndAddNewTask(subtask, original);
        calculateEpicStatus(epics.get(original.getEpicId()));
        subtasks.put(subtask.getId(), subtask);
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
        if (epic != null && epic.getSubtasks() != null) {
            return epic.getSubtasks();
        }
        return new ArrayList<>();
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
        if (epic != null) {
            historyManager.add(epic);
        }
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

    private void checkTaskTime(Task task) {
        for (Task t : prioritizedTasks) {
            if (t.getId() == task.getId()) {
                continue;
            }
            if (isOverlapping(t, task)) throw new ValidationException("Пересечение с задачей");
        }
    }

    private void deleteOldAndAddNewTask(Task epic, Task original) {
        checkTaskTime(epic);
        prioritizedTasks.remove(original);
        prioritizedTasks.add(epic);
    }

    private void calculateEpicStatus(Epic epic) {
        Status status = Status.NEW;
        LocalDateTime start = LocalDateTime.MAX;
        Duration duration = Duration.ZERO;
        LocalDateTime end = LocalDateTime.MIN;

        boolean isInProgress = false;
        boolean isDone = false;


        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStartTime().isBefore(start)) {
                start = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(end)) {
                end = subtask.getEndTime();
            }
            if (subtask.getStatus() == Status.NEW) {
                status = Status.NEW;
            } else if (subtask.getStatus() == Status.IN_PROGRESS) {
                isInProgress = true;
            } else if (subtask.getStatus() == Status.DONE) {
                isDone = true;
            }
            duration = duration.plus(subtask.getDuration());
        }
        if (isInProgress) {
            status = Status.IN_PROGRESS;
        } else if (isDone &&!isInProgress) {
            status = Status.DONE;
        }
        epic.setStartTime(start);
        epic.setEndTime(end);
        epic.setStatus(status);
        epic.setDuration(duration);

    }

    private boolean isOverlapping(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
    }
}




