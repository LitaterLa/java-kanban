package com.yandex.add.server;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.add.exceptions.NotFoundException;
import com.yandex.add.model.Task;
import com.yandex.add.service.taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (Pattern.matches("/history/?", path) && ("GET".equals(method))) {
            try {
                List<Task> history = taskManager.getHistory();
                sendResponse(exchange, 200, gson.toJson(history));
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Метод или путь не найден");
            }

        }

    }
}

