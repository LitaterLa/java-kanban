package com.yandex.add.service.tasks;

import com.yandex.add.exceptions.ManagerSaveException;
import com.yandex.add.model.*;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                final int id = task.getIdNum();
                if (task.getType() == TaskType.EPIC) {
                    epics.put(id, (Epic) task);
                } else if (task.getType() == TaskType.SUBTASK) {
                    subtasks.put(id, (Subtask) task);
                } else if (task.getType() == TaskType.TASK) {
                    tasks.put(id, task);
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
        } catch (IOException e) {
            throw new IOException("Файл не найден", e);
        }
        manager.seq = maxId;
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    private String toString(Task task) {
        if (task == null) return "No such task";
        final String taskType;
        if (task.getType().equals(TaskType.SUBTASK)) {
            taskType = TaskType.SUBTASK.toString();
            return task.getIdNum() + "," + taskType + "," + task.getTitle() + "," + task.getDescription() + "," + task.getTaskStatus().toString() + "," + ((Subtask) task).getEpicId();
        } else if (task.getType().equals(TaskType.EPIC)) {
            taskType = TaskType.EPIC.toString();
        } else {
            taskType = TaskType.TASK.toString();
        }
        return task.getIdNum() + "," + taskType + "," + task.getTitle() + "," + task.getTaskStatus().toString() + "," + task.getDescription();
    }

    private static Task fromString(String value) {
        if (value == null || value.isEmpty() || value.isBlank()) return null;
        String[] data = value.split("\n");
        String[] newData;
        for (String datum : data) {
            newData = datum.split(",");
            try {
                String taskTypeString = newData[1].trim().toUpperCase();
                TaskType type = TaskType.valueOf(taskTypeString);
                String title = newData[2];
                String description = newData[4];
                int id = Integer.parseInt(newData[0].trim());
                if (type == TaskType.TASK) {
                    Task task = new Task(title, description);
                    task.setIdNum(id);
                    task.setTaskStatus(parseTaskStatus(newData[3].trim()));
                    return task;
                } else if (type == TaskType.EPIC) {
                    Epic epic = new Epic(title, description);
                    epic.setIdNum(id);
                    epic.setTaskStatus(parseTaskStatus(newData[3].trim()));
                    return epic;
                } else if (type == TaskType.SUBTASK) {
                    Subtask subtask = new Subtask(title, description, Integer.parseInt(newData[5]));
                    subtask.setIdNum(id);
                    subtask.setTaskStatus(parseTaskStatus(newData[3].trim()));
                    return subtask;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Тип задач не найден");
            }

        }
        return null;
    }

    private static TaskStatus parseTaskStatus(String status) {
        return TaskStatus.valueOf(status.toUpperCase());
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : getTasks()) {
                writer.write(toString(task));
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении задачи", exception);
        }
    }

}

