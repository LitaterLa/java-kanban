package com.yandex.add.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Epic, ArrayList<Subtask>> epicsWithSubtasks = new HashMap<>();
    private int idCounter = 1;

    private int generateId() {
        return ++idCounter;
    }


    public Task createTask(Task task) {
        int id = generateId();
        task.setIdNum(generateId());
        taskHashMap.put(task.getIdNum(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setIdNum(generateId());
        epicHashMap.put(epic.getIdNum(), epic);
        epicsWithSubtasks.put(epic, new ArrayList<>());
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setIdNum(generateId());
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic != null) {
            subtaskHashMap.put(subtask.getIdNum(), subtask);
            epicsWithSubtasks.get(epic).add(subtask);
        }
        return subtask;
    }

    public void deleteAll() {
        taskHashMap.clear();
        subtaskHashMap.clear();
        epicHashMap.clear();
        epicsWithSubtasks.clear();
        System.out.println("Deleted!");
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
        System.out.println("Deleted!");
    }

    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
        epicsWithSubtasks.values().clear();

        // for (ArrayList <Subtask> subtasks : epicWithSubtasks.values()) {
        // subtasks.clear();
        // }
        System.out.println("Deleted!");
    }

    public void deleteAllEpics() {
        epicHashMap.clear();
        epicsWithSubtasks.clear();
        System.out.println("Deleted!");
    }

    public void deleteTaskById(int id) {
        if (taskHashMap.get(id) != null) {
            taskHashMap.remove(id);
        }
    }

    public void deleteEpicById(int id) {
        Epic epicToRemove = epicHashMap.get(id);
        if (epicToRemove == null) {
            System.out.println("Not found");
        }

        ArrayList<Subtask> subtasks = epicsWithSubtasks.remove(epicToRemove);
        if (subtasks != null) {
            for (Subtask subtask : subtasks) {
                subtaskHashMap.remove(subtask.getIdNum());
            }
        }
    }

    public void deleteSubtaskById(int id) {
        for (Epic epic : epicsWithSubtasks.keySet()) {
            ArrayList<Subtask> subtasks = epicsWithSubtasks.get(epic);
            ArrayList<Subtask> updSubtasks = new ArrayList<>();
            for (Subtask subtask : subtasks) {
                if (subtask.getIdNum() != id) {
                    updSubtasks.add(subtask);
                }
            }
            if (updSubtasks.size() != subtasks.size()) {
                epicsWithSubtasks.put(epic, updSubtasks);
            }
        }

    }

    public void updateTask(Task task) {
        taskHashMap.put(task.getIdNum(), task);

    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = epicHashMap.get(epic.getIdNum());
        oldEpic.setDescription(epic.getDescription());
        oldEpic.setTitle(epic.getTitle());
        epicHashMap.put(epic.getIdNum(), oldEpic);
    }

    public void updateSubtack(Subtask subtask) {
        subtaskHashMap.put(subtask.getIdNum(), subtask);

        ArrayList<Subtask> subtasks = epicsWithSubtasks.get(subtask);
        boolean isReady = false;
        boolean isInProgress = false;
        if (subtasks != null) {
            for (Subtask st : subtasks) {
                if (st.getIdNum() == subtask.getIdNum()) {
                    st.setTitle(subtask.getTitle());
                    st.setDescription(subtask.getDescription());
                    st.setTaskStatus(subtask.getTaskStatus());
                }

                if (st.getTaskStatus().equals(TaskStatus.IN_PROGRESS)) {
                    isInProgress = true;
                } else if (st.getTaskStatus().equals(TaskStatus.DONE)) {
                    isReady = true;
                }
            }

        }
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (isReady) {
            epic.setTaskStatus(subtask.getTaskStatus());
        } else if (isInProgress) {
            epic.setTaskStatus(subtask.getTaskStatus());
        }

    }


    public HashMap<Integer, Task> printTask() {
        if (taskHashMap.isEmpty()) {
            System.out.println("nothing here");
            return null;
        }
        System.out.println(taskHashMap.toString());

        return taskHashMap;
    }

    public HashMap printEpicsAndSubtasks() {
        if (epicsWithSubtasks.isEmpty()) {
            System.out.println("nothing here");
            return epicsWithSubtasks;
        }
        System.out.println(epicsWithSubtasks.toString());

        return null;
    }

    public Task getTaskById(int id) {
        return taskHashMap.get(id);
    }

    public Task getEpicByID(int id) {
        return epicHashMap.get(id);
    }

    public Task getSubtaskByID(int id) {
        return subtaskHashMap.get(id);
    }

    public Subtask getSubtasksByEpic(Epic epic) {
        return subtaskHashMap.get(epic.getIdNum());
    }

    public void setEpicStatus(Epic epic) {
        for (Epic ep : epicHashMap.values()) {
            ArrayList<Subtask> tempSubtasks = epicsWithSubtasks.get(ep);
            if (tempSubtasks == null || tempSubtasks.isEmpty()) {
                ep.setTaskStatus(TaskStatus.NEW);
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
                }

                if (isDone == true) {
                    ep.setTaskStatus(TaskStatus.DONE);
                } else if (isNew == true) {
                    ep.setTaskStatus(TaskStatus.NEW);
                } else {
                    ep.setTaskStatus(TaskStatus.IN_PROGRESS);
                }
            }
        }
    }
}


