package com.yandex.add.service.taskManager;

import com.yandex.add.exceptions.ManagerSaveException;
import com.yandex.add.model.*;
import com.yandex.add.service.history.HistoryManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

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
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task == null) {
                    System.out.println("Ошибка: не удалось распознать строку - " + line);
                    continue;
                }

                int id = task.getIdNum();
                switch (task.getType()) {
                    case EPIC:
                        manager.epics.put(id, (Epic) task);
                        manager.epicsWithSubtasks.put((Epic) task, new ArrayList<>());
                        break;
                    case SUBTASK:
                        manager.subtasks.put(id, (Subtask) task);
                        Epic epic = manager.epics.get(((Subtask) task).getEpicId());
                        List<Subtask> subtasks = new ArrayList<>();
                        if (epic != null) {
                            for (Subtask subtask : manager.subtasks.values()) {
                                if (subtask.getEpicId() == epic.getIdNum()) {
                                    subtasks.add(subtask);
                                }
                            }
                        }
                        manager.epicsWithSubtasks.put(epic, subtasks);
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
            }
        } catch (IOException e) {
            throw new ManagerSaveException("file loading failed,", e);
        }
        seq = maxId;
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        seq = task.getIdNum();
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        seq = epic.getIdNum();
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        seq = subtask.getIdNum();
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
        String result = task.getIdNum() + "," + taskType + "," + task.getTitle() + "," + task.getTaskStatus().toString() + "," + task.getDescription();

        if (task.getType().equals(TaskType.SUBTASK)) {
            result += "," + ((Subtask) task).getEpicId();
        }

        return result;
    }


    private static Task fromString(String value) {
        if (value.isEmpty() || value.isBlank()) return null;
        String[] newData = value.split(",");
        try {
            TaskType type = TaskType.valueOf(newData[1].trim().toUpperCase());
            String title = newData[2].trim();
            String description = newData[4].trim();
            int id = Integer.parseInt(newData[0].trim());

            TaskStatus taskStatus = parseTaskStatus(newData[3].trim());
            if (type == TaskType.TASK) {
                Task task = new Task(title, description);
                task.setIdNum(id);
                task.setTaskStatus(taskStatus);
                return task;
            } else if (type == TaskType.EPIC) {
                Epic epic = new Epic(title, description);
                epic.setIdNum(id);
                epic.setTaskStatus(taskStatus);
                return epic;
            } else if (type == TaskType.SUBTASK) {
                Subtask subtask = new Subtask(title, description, Integer.parseInt(newData[5].trim()));
                subtask.setIdNum(id);
                subtask.setTaskStatus(taskStatus);
                return subtask;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при разборе строки: " + value);
            e.printStackTrace();
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
