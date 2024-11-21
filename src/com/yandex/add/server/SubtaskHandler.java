package com.yandex.add.server;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.add.exceptions.NotFoundException;
import com.yandex.add.model.Subtask;
import com.yandex.add.service.taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("/subtasks/?", path)) {
            handleSubtasks(httpExchange, method);
        } else if (Pattern.matches("/subtasks/\\d+", path)) {
            handleSubtaskById(httpExchange, method, path);
        } else {
            sendResponse(httpExchange, 404, "Путь не найден ");
        }

    }

    private void handleSubtaskById(HttpExchange httpExchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                try {
                    Subtask subtask = taskManager.getSubtaskByID(extractId(path));
                    sendResponse(httpExchange, 200, gson.toJson(subtask));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "POST":
                try {
                    Subtask updatedSubtask = gson.fromJson(readText(httpExchange), Subtask.class);
                    taskManager.updateSubtask(updatedSubtask);
                    sendResponse(httpExchange, 200, gson.toJson(updatedSubtask));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    Integer id = extractId(path);
                    taskManager.deleteSubtaskById(id);
                    sendResponse(httpExchange, 200, "Подзадача id " + id + " удалена");
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            default:
                sendNotFound(httpExchange, "Метод не найден");
                break;
        }
    }

    private void handleSubtasks(HttpExchange httpExchange, String method) throws IOException {
        switch (method) {
            case "GET":
                List<Subtask> subtasks = taskManager.getSubtasks();
                sendResponse(httpExchange, 200, gson.toJson(subtasks));
                break;
            case "POST":
                try {
                    Subtask newSubtask = gson.fromJson(readText(httpExchange), Subtask.class);
                    Subtask createdSubtask = taskManager.createSubtask(newSubtask);
                    sendResponse(httpExchange, 201, gson.toJson(createdSubtask));
                } catch (IllegalArgumentException e) {
                    sendHasInteraction(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                taskManager.deleteSubtasks();
                sendResponse(httpExchange, 200, "Все подзадачи удалены");
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
