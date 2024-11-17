package com.yandex.add.server;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.add.exceptions.NotFoundException;
import com.yandex.add.model.Task;
import com.yandex.add.service.taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("/tasks/?", path)) {
            handleTasks(httpExchange, method);
        } else if (Pattern.matches("/tasks/\\d+", path)) {
            handleTaskById(httpExchange, method, path);
        } else {
            sendResponse(httpExchange, 404, "Путь не найден ");
        }

    }

    private void handleTasks(HttpExchange httpExchange, String method) throws IOException {
        switch (method) {
            case "GET":
                List<Task> tasks = taskManager.getTasks();
                sendResponse(httpExchange, 200, gson.toJson(tasks));
                break;
            case "POST":
                try {
                    Task newTask = gson.fromJson(readText(httpExchange), Task.class);
                    Task createdTask = taskManager.createTask(newTask);
                    sendResponse(httpExchange, 201, gson.toJson(createdTask));
                } catch (IllegalArgumentException e) {
                    sendHasInteraction(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                taskManager.deleteTasks();
                sendResponse(httpExchange, 200, "Все задачи удалены");
                break;
            default:
                sendNotFound(httpExchange, "Метод не найден");
                break;
        }
    }

    private void handleTaskById(HttpExchange httpExchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                try {
                    Task task = taskManager.getTaskById(extractId(path));
                    sendResponse(httpExchange, 200, gson.toJson(task));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "POST":
                try {
                    Task updatedTask = gson.fromJson(readText(httpExchange), Task.class);
                    taskManager.updateTask(updatedTask);
                    sendResponse(httpExchange, 200, gson.toJson(updatedTask));

                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    Integer id = extractId(path);
                    taskManager.deleteTaskById(id);
                    sendResponse(httpExchange, 200, "Задача id " + id + " удалена");
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            default:
                sendNotFound(httpExchange, "Метод не найден");
                break;
        }
    }

    private Integer extractId(String path) {
        String[] splitPath = path.split("/");
        return Integer.parseInt(splitPath[splitPath.length - 1]);
    }
}
