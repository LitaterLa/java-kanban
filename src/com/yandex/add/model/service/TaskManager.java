package com.yandex.add.model.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Epic, ArrayList<Subtask>> epicsWithSubtasks = new HashMap<>();
    private int idCounter = 1;

    public int generateId() {
        return ++idCounter;
    }
    public Task createTask(Task task) {
        Task task1 = new Task(task.getTitle(), task.getDescription(), task.getIdNum());
        taskHashMap.put( task.getIdNum(), task1);
        return task1;
    }
    public Epic createEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getIdNum());
        epicHashMap.put(epic.getIdNum(), newEpic);
        epicsWithSubtasks.put(newEpic, new ArrayList<>());
        return newEpic;
    }
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getIdNum(), subtask.getEpicId());
        subtaskHashMap.put(subtask.getIdNum(), newSubtask);
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic != null) {
            epicsWithSubtasks.get(epic).add(newSubtask);
        } else {
            System.out.println("Epic id num. " + epic.getIdNum()+ " not found");
        }
        return newSubtask;
    }

    public void deleteAll() {
        taskHashMap.clear();
        subtaskHashMap.clear();
        epicHashMap.clear();
        System.out.println("Deleted!");
    }

    public void deleteAllTasks() {
        taskHashMap.clear();
        System.out.println("Deleted!");
    }

    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
        System.out.println("Deleted!");
    }

    public void deleteAllEpics() {
        epicHashMap.clear();
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
        if(subtasks != null){
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
        epicHashMap.put(epic.getIdNum(), epic);
    }

    public void updateSubtack(Subtask subtask) {
        subtaskHashMap.put(subtask.getIdNum(), subtask);
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
        for (Task task : taskHashMap.values()) {
            if (task.getIdNum() == id) {
                return task;
            }
        }
        return null;
    }

    public String getSubtasksByEpic(Epic epic) {
        if (subtaskHashMap != null) {
            return subtaskHashMap.toString();
        }
        return "No such subtask";
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


