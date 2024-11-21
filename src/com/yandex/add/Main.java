package com.yandex.add;

import com.google.gson.Gson;
import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.history.InMemoryHistoryManager;
import com.yandex.add.service.taskmanager.InMemoryTaskManager;

public class Main {
    public static void main(String[] args) {

        // Создаем менеджеры
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);

// Создаем задачи
        Task task1 = new Task("Parent-teacher conf", "school year org");
        taskManager.createTask(task1);
        Task task2 = new Task("Shopping", "water filter");
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Java", "learn coding");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Javaa", "learn coding");
        taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Master static modifier", "solve more problems", epic1.getId());
        taskManager.createSubtask(subtask1);

// Получаем задачи
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskByID(subtask1.getId());

// Создаем новый менеджер
        InMemoryTaskManager newManager = new InMemoryTaskManager(historyManager);

// Проверяем историю и размеры
        System.out.println(historyManager.getHistorySize()); // Должно быть 4 (если все добавлено)
        System.out.println(newManager.getTasks().size()); // Должно быть 0
        System.out.println(taskManager.getTasks().size()); // Должно быть 3

        Gson gson = new Gson();

    }
}
