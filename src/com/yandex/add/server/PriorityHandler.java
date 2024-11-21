package com.yandex.add.server;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.add.exceptions.NotFoundException;
import com.yandex.add.model.Task;
import com.yandex.add.service.taskmanager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class PriorityHandler extends BaseHttpHandler {
    public PriorityHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        if (Pattern.matches("/prioritized/?", path) && "GET".equals(method)) {
            try {
                List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                sendResponse(exchange, 200, gson.toJson(prioritizedTasks));
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Приоритетные задачи не найдены");
            }

        }
    }
}
