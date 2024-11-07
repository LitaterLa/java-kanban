package com.yandex.add.service.taskManager;

import com.yandex.add.exceptions.ManagerSaveException;
import com.yandex.add.model.*;
import com.yandex.add.service.history.HistoryManager;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epicId,startTime,duration";

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

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

        manager.seq = maxId;
        return manager;
    }



    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        seq = task.getId();
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        seq = epic.getId();
       // save();
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
        seq = subtask.getId();
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
        if (value.isEmpty() || value.isBlank() || value.equals("id,type,name,status,description,epicId,startTime,duration")) return null;
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
                Task task = new Task(id,title, status, description, startTime, duration);
                return task;
            } else if (type == TaskType.EPIC) {
                Epic epic = new Epic(id,title, status, description, startTime, duration);
                return epic;
            } else if (type == TaskType.SUBTASK) {
                Subtask subtask = new Subtask(id, title, status, description, Integer.parseInt(epicId), startTime, duration);
                return subtask;
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
