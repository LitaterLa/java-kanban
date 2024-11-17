package com.yandex.add.server;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.add.exceptions.NotFoundException;
import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.service.taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String path = httpExchange.getRequestURI().getPath();

        if (Pattern.matches("/epics/?", path)) {
            handleEpics(httpExchange, method);
        } else if (Pattern.matches("/epics/\\d+", path)) {
            handleEpicById(httpExchange, method, path);
        } else if (Pattern.matches("/epics/\\d+/subtasks", path)) {
            handleEpicAndGetItsSubtask(httpExchange, method, path);
        } else {
            sendResponse(httpExchange, 404, "Путь не найден ");
        }

    }

    private void handleEpicAndGetItsSubtask(HttpExchange httpExchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                try {
                    Epic epic = taskManager.getEpicById(extractId(path));
                    List<Subtask> subtasks = epic.getSubtasks();
                    sendResponse(httpExchange, 200, gson.toJson(subtasks));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, "Эпик не найден");
                }
                break;
            case "DELETE":
                try {
                    Integer id = extractId(path);
                    Epic epic = taskManager.getEpicById(id);
                    List<Subtask> subtasks = epic.getSubtasks();
                    subtasks.removeIf(subtask -> subtask.getEpicId() == id);
                    epic.setSubtasks(subtasks);
                    taskManager.updateEpic(epic);
                    sendResponse(httpExchange, 200, gson.toJson(epic));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, "Эпик не найден");
                }
                break;
            default:
                sendNotFound(httpExchange, "Метод не найден");
                break;
        }

    }

    private void handleEpicById(HttpExchange httpExchange, String method, String path) throws IOException {
        switch (method) {
            case "GET":
                try {
                    Epic epic = taskManager.getEpicById(extractId(path));
                    sendResponse(httpExchange, 200, gson.toJson(epic));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "POST":
                try {
                    Epic updatedEpic = gson.fromJson(readText(httpExchange), Epic.class);
                    taskManager.updateEpic(updatedEpic);
                    sendResponse(httpExchange, 200, gson.toJson(updatedEpic));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    Integer id = extractId(path);
                    taskManager.deleteEpicById(id);
                    sendResponse(httpExchange, 200, "Эпик id " + id + " удалена");
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange, e.getMessage());
                }
                break;
            default:
                sendNotFound(httpExchange, "Метод не найден");
                break;
        }
    }

    private void handleEpics(HttpExchange httpExchange, String method) throws IOException {
        switch (method) {
            case "GET":
                List<Epic> epics = taskManager.getEpics();
                sendResponse(httpExchange, 200, gson.toJson(epics));
                break;
            case "POST":
                try {
                    Epic newEpic = gson.fromJson(readText(httpExchange), Epic.class);
                    Epic createdEpic = taskManager.createEpic(newEpic);
                    sendResponse(httpExchange, 201, gson.toJson(createdEpic));
                } catch (IllegalArgumentException e) {
                    sendHasInteraction(httpExchange, e.getMessage());
                }
                break;
            case "DELETE":
                taskManager.deleteEpics();
                sendResponse(httpExchange, 200, "Все эпики удалены");
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
