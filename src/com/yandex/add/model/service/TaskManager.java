package com.yandex.add.model.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    final ArrayList<Task> taskArrayList = new ArrayList<>();
    final HashMap<Epic, ArrayList<Subtask>> epicsWithSubtasks = new HashMap<>();
    private int idCounter = 1;

    private int generateId() {
        return ++idCounter;
    }

    public Task createTask(String title, String description) {
        Task task = new Task(title, description, generateId());
        taskArrayList.add(task);
        return task;
    }

    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(title, description, generateId());
        taskArrayList.add(epic);
        epicsWithSubtasks.put(epic, new ArrayList<>());
        return epic;
    }

    public Subtask createSubtask(String title, String description, Epic epic) {
        Subtask subtask = new Subtask(title, description, generateId());
        taskArrayList.add(subtask);
        epicsWithSubtasks.get(epic).add(subtask);
        return subtask;
    }

    public void deleteAll() {
        taskArrayList.clear();
        epicsWithSubtasks.clear();
        System.out.println("Deleted!");
    }

    public void deleteTaskById(int id) {
        for (int i = taskArrayList.size() - 1; i >= 0; i--) {
            if (taskArrayList.get(i).getIdNum() == id) {
                taskArrayList.remove(i);
            }
        }
    }

    public void deleteEpicById(int id) {
        ArrayList<Epic> epicsToRemove = new ArrayList<>();
        for (Epic epic : epicsWithSubtasks.keySet()) {
            if (epic.getIdNum() == id) {
                epicsToRemove.add(epic);
            }
        }
        for (Epic epic : epicsToRemove) {
            epicsWithSubtasks.remove(epicsToRemove);
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

    public Task changeSubtaskStatus(Subtask subtask, TaskStatus newStatus) {
        for (Epic epic : epicsWithSubtasks.keySet()) {
            ArrayList<Subtask> subtasks = epicsWithSubtasks.get(epic);
            for (Subtask subtask1 : subtasks) {
                if (subtask1.equals(subtask)) {
                    subtask1.setTaskStatus(newStatus);
                    return subtask1;
                }
            }
        }
        return null;
    }


    public Task changeTaskStatus(Task task, TaskStatus newStatus) {
        for (Task task1 : taskArrayList) {
            if (task1.equals(task)) {
                task1.setTaskStatus(newStatus);
                return task1;
            }
        }
        return null;
    }

    public String printTask() {
        if (taskArrayList.isEmpty()) {
            return "Nothing found.";
        }
        return taskArrayList.toString();
    }

    //+
    public String printEpicsAndSubtasks() {
        if (!(epicsWithSubtasks.isEmpty())) {
            return epicsWithSubtasks.toString();
        }
        return "Nothing found";
    }

    public Task getTaskById(int id) {
        for (Task task : taskArrayList) {
            if (task.getIdNum() == id) {
                return task;
            }
        }
        return null;
    }


    public void updateTask(int id, Task newTask) {
        for (Task task : taskArrayList) {
            if (task.getIdNum() == id) {
                task = newTask;
                return;
            }
        }

    }

    public String getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasks = epicsWithSubtasks.get(epic);
        if (subtasks != null) {
            return subtasks.toString();
        }
        return "No such epic";
    }

    public void setEpicStatus(Epic epic) {
        for (Epic ep : epicsWithSubtasks.keySet()) {
            ArrayList<Subtask> tempSubtasks = epicsWithSubtasks.get(ep);
            if (tempSubtasks == null || tempSubtasks.isEmpty()) { // check by null and empty
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


