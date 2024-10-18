package com.yandex.add.service;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    File file;


    public FileBackedTaskManager(File file) {
        this.file = file; // здесь может быть идет ошибка из предыдущих спринтов,
    }                     // в InMemoryTaskManager был 1 конструктор с параметром HistoryManager

    static class ManagerSaveException extends IOException {

        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<Task> tasks = new ArrayList<>();
        List<Epic> epics = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) { // пытаюсь сохранить тип объекта
                Task task = fromString(line);
                if (task instanceof Epic) {
                    epics.add((Epic) task);
                } else if (task instanceof Subtask) {
                    subtasks.add((Subtask) task);
                } else if (task != null) {
                    tasks.add(task);
                }
            }
            for (Task task : tasks) {
                manager.createTask(task);
            }
            for (Epic epic : epics) {
                manager.createEpic(epic);
            }
            for (Subtask subtask : subtasks) {
                manager.createSubtask(subtask);
            }
        } catch (IOException e) {
            throw new IOException("Файл не найден", e);
        }
        return manager;
    }

    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws ManagerSaveException {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) throws ManagerSaveException {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) throws ManagerSaveException {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) throws ManagerSaveException {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
    }

    private String toString(Task task) {
        if (task == null) return "No such task";
        String taskType;
        if (task instanceof Subtask) {
            taskType = TaskTypes.SUBTASK.toString();
            return task.getIdNum() + "," + taskType + "," + task.getTitle() + "," + task.getDescription() + "," + task.getTaskStatus().toString() + "," + ((Subtask) task).getEpicId();
        } else if (task instanceof Epic) {
            taskType = TaskTypes.EPIC.toString();
        } else {
            taskType = TaskTypes.TASK.toString();
        }
        return task.getIdNum() + "," + taskType + "," + task.getTitle() + "," + task.getTaskStatus().toString() + "," + task.getDescription();
    }

    private static Task fromString(String value) {
        if (value == null || value.isEmpty() || value.isBlank()) return null;
        String[] data = value.split("\n");
        String[] newData;
        for (String datum : data) {
            newData = datum.split(",");
            if (newData[1].trim().equalsIgnoreCase("task")) {
                Task task = new Task(newData[2], newData[4]);
                task.setIdNum(Integer.parseInt(newData[0].trim()));
                task.setTaskStatus(parseTaskStatus(newData[3].trim()));
                return task;
            } else if (newData[1].trim().equalsIgnoreCase("epic")) {
                Epic epic = new Epic(newData[2], newData[4]);
                epic.setIdNum(Integer.parseInt(newData[0].trim()));
                epic.setTaskStatus(parseTaskStatus(newData[3].trim()));
                return epic;
            } else if (newData[1].trim().equalsIgnoreCase("subtask")) {
                Subtask subtask = new Subtask(newData[2], newData[4], Integer.parseInt(newData[5]));
                subtask.setIdNum(Integer.parseInt(newData[0].trim()));
                subtask.setTaskStatus(parseTaskStatus(newData[3].trim()));
                return subtask;
            }
        }
        return null;
    }

    private static TaskStatus parseTaskStatus(String status) {
        switch (status.toLowerCase()) {
            case "done":
                return TaskStatus.DONE;
            case "new":
                return TaskStatus.NEW;
            case "in_progress":
                return TaskStatus.IN_PROGRESS;
            default:
                throw new IllegalArgumentException("Неизвестный статус задачи: " + status);
        }
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : getTasks()) {
                writer.write("\n" + toString(task));
            }
            for (Epic epic : getEpics()) {
                writer.write("\n" + toString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write("\n" + toString(subtask));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении задачи", exception);
        }
    }

    private enum TaskTypes {
        TASK, EPIC, SUBTASK
    }

}

