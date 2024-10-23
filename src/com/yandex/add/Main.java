package com.yandex.add;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.historyManager.InMemoryHistoryManager;
import com.yandex.add.service.taskManager.FileBackedTaskManager;
import com.yandex.add.service.taskManager.InMemoryTaskManager;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
        File file = new File("template");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Parent-teacher conf", "school year org");
        Task task2 = new Task("Shopping", "water filter");
        fileManager.createTask(task1);
        fileManager.createTask(task2);

        Epic epic1 = new Epic("Java", "learn coding");
        fileManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Master static modifier", "solve more problems", epic1.getIdNum());
        Subtask subtask2 = new Subtask("Study OOP closer", "solve more problems", epic1.getIdNum());
        Subtask subtask3 = new Subtask("string", "string class", epic1.getIdNum());
        fileManager.createSubtask(subtask2);
        fileManager.createSubtask(subtask1);
        fileManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Efficient tutoring", "enhance st skills");
        fileManager.createEpic(epic2);

        FileBackedTaskManager fileBackedTaskManager = fileManager.loadFromFile(file);

//        taskManager.getEpicByID(epic1.getIdNum());
//        System.out.println(historyManager.getHistory().size());
//        taskManager.getSubtaskByID(subtask1.getIdNum());
//        System.out.println(historyManager.getHistory().size());
//        taskManager.getSubtaskByID(subtask3.getIdNum());
//        System.out.println(historyManager.getHistory().size());
//
//        taskManager.getEpicByID(epic2.getIdNum());
//        taskManager.getTaskById(task2.getIdNum());
//
//        System.out.println(historyManager.getHistory().size());
//        taskManager.getTaskById(task1.getIdNum());
//        taskManager.getSubtaskByID(subtask1.getIdNum());
//        taskManager.getSubtaskByID(subtask3.getIdNum());
//
//        System.out.println(historyManager.getHistory().size());
//
//        historyManager.remove(task1.getIdNum());
//        System.out.println(historyManager.getHistory().size());
//
//        taskManager.deleteEpicById(epic1.getIdNum());
//        historyManager.remove(epic1.getIdNum());
//        historyManager.remove(subtask1.getIdNum());
//        historyManager.remove(subtask3.getIdNum());
//
//        System.out.println(historyManager.getHistory().size());

    }
}
