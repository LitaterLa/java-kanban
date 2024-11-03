package com.yandex.add;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.history.InMemoryHistoryManager;
import com.yandex.add.service.taskManager.InMemoryTaskManager;

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
        Subtask subtask1 = new Subtask("Master static modifier", "solve more problems", epic1.getIdNum());
        taskManager.createSubtask(subtask1);

// Получаем задачи
        taskManager.getTaskById(task1.getIdNum());
        taskManager.getTaskById(task2.getIdNum());
        taskManager.getEpicByID(epic1.getIdNum());
        taskManager.getSubtaskByID(subtask1.getIdNum());

// Создаем новый менеджер
        InMemoryTaskManager newManager = new InMemoryTaskManager(historyManager);

// Проверяем историю и размеры
        System.out.println(historyManager.getHistorySize()); // Должно быть 4 (если все добавлено)
        System.out.println(newManager.getTasks().size()); // Должно быть 0
        System.out.println(taskManager.getTasks().size()); // Должно быть 3

//        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
//        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
//        private static File file = new File("mytest");
//        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
//        Task task1 = new Task("Parent-teacher conf", "school year org");
//        fileManager.createTask(task1);
//        Task task2 = new Task("Shopping", "water filter");
//        fileManager.createTask(task2);
//        Epic epic1 = new Epic("Java", "learn coding");
//        fileManager.createEpic(epic1);
//        Epic epic2 = new Epic("Javaa", "learn coding");
//        fileManager.createEpic(epic2);
//        Subtask subtask1 = new Subtask("Master static modifier", "solve more problems", epic1.getIdNum());
//        fileManager.createSubtask(subtask1);
//
//        taskManager.createTask(task1);
//        taskManager.createTask(task2);
//        taskManager.createTask(epic1);
//        taskManager.getTaskById(task1.getIdNum());
//        taskManager.getTaskById(task2.getIdNum());
//        taskManager.getEpicByID(epic1.getIdNum());
//        taskManager.getSubtaskByID(subtask1.getIdNum());
//
//        InMemoryTaskManager newManager = new InMemoryTaskManager(historyManager);
//        System.out.println(historyManager.getHistorySize());
//        System.out.println(newManager.getTasks().size());
//        System.out.println(taskManager.getTasks().size());
//
//
////        FileBackedTaskManager newFileManager = fileManager.loadFromFile(file);
////        System.out.println(newFileManager.getEpics().size());
////        Task newTask = newFileManager.getTaskById(task1.getIdNum());
////        Epic newEpic = newFileManager.getEpicByID(epic1.getIdNum());
////        Subtask newSubtask = newFileManager.getSubtaskByID(subtask1.getIdNum());
////        System.out.println(subtask1.getIdNum() + "is" + newSubtask.getIdNum());
////
////        fileManager.deleteEpicById(epic1.getIdNum());
////        System.out.println(fileManager.getEpics().size());
////        System.out.println(fileManager.getEpics().get(epic2.getIdNum()).getTitle());

    }
}
