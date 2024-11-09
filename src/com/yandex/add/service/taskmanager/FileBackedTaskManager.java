package com.yandex.add.service.taskmanager;

import com.yandex.add.exceptions.ManagerSaveException;
import com.yandex.add.model.Epic;
import com.yandex.add.model.Status;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.model.TaskType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epicId,startTime,duration";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Пропускаем заголовок
            line = reader.readLine();
            if (line != null && line.equals(HEADER)) {
                line = reader.readLine();
            }

            // Загружаем задачи, эпики и подзадачи
            while (line != null) {
                Task task = fromString(line);
                if (task == null) {
                    System.out.println("Ошибка: не удалось распознать строку - " + line);
                    line = reader.readLine();
                    continue;
                }

                int id = task.getId();
                switch (task.getType()) {
                    case EPIC:
                        manager.epics.put(id, (Epic) task);
                        break;
                    case SUBTASK:
                        manager.subtasks.put(id, (Subtask) task);
                        // Добавляем подзадачу в эпик
                        Epic epic = manager.epics.get(task.getEpicId());
                        if (epic != null) {
                            epic.addSubtask((Subtask) task);
                        }
                        break;
                    case TASK:
                        manager.tasks.put(id, task);
                        break;
                    default:
                        System.out.println("Неизвестный тип задачи: " + task.getType());
                        break;
                }
                if (maxId < id) {
                    maxId = id;
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("file loading failed,", e);
        }

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
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        Epic associatedEpic = epics.get(subtask.getEpicId());
        if (associatedEpic != null) {
            associatedEpic.addSubtask(newSubtask);
            System.out.println("Подзадача " + newSubtask + " успешно добавлена к эпику " + associatedEpic);
        } else {
            System.out.println("Ошибка: эпик с id " + subtask.getEpicId() + " не найден.");
        }
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

        String taskType = task.getType().toString();

        return task.getId() + "," + taskType + "," + task.getTitle() + ","
                + task.getStatus().toString() + "," + task.getDescription() + ","
                + task.getEpicId() + "," + task.getStartTime() + "," + task.getDuration();
    }


    private static Task fromString(String value) {
        if (value.isEmpty() || value.isBlank() || value.equals(HEADER)) return null;
        String[] newData = value.split(",");
        try {
            TaskType type = TaskType.valueOf(newData[1].trim().toUpperCase());
            String title = newData[2].trim();
            String description = newData[4].trim();
            int id = Integer.parseInt(newData[0].trim());
            Status status = parseTaskStatus(newData[3].trim());
            String epicId = newData[5].trim();
            LocalDateTime startTime = LocalDateTime.parse(newData[6]);
            Duration duration = Duration.parse(newData[7]);
            if (type == TaskType.TASK) {
                return new Task(id, title, status, description, startTime, duration);
            } else if (type == TaskType.EPIC) {
                return new Epic(id, title, status, description, startTime, duration);
            } else if (type == TaskType.SUBTASK) {
                return new Subtask(id, title, status, description, Integer.parseInt(epicId), startTime, duration);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при разборе строки: " + value);
            e.printStackTrace();
        }
        return null;
    }

    private static Status parseTaskStatus(String status) {
        return Status.valueOf(status.toUpperCase());
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER + "\n");
            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении задачи", exception);
        }
    }
}
