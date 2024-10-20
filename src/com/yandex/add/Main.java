package com.yandex.add;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.history.InMemoryHistoryManager;
import com.yandex.add.service.tasks.FileBackedTaskManager;
import com.yandex.add.service.tasks.InMemoryTaskManager;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
        File file = new File("mytest");
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Parent-teacher conf", "school year org");
        fileManager.createTask(task1);
        Task task2 = new Task("Shopping", "water filter");
        fileManager.createTask(task2);
        Epic epic1 = new Epic("Java", "learn coding");
        fileManager.createEpic(epic1);
        Epic epic2 = new Epic("Javaa", "learn coding");
        fileManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Master static modifier", "solve more problems", epic1.getIdNum());
        fileManager.createSubtask(subtask1);

        FileBackedTaskManager newFileManager = fileManager.loadFromFile(file);
//        System.out.println(newFileManager.getSubtasks().size());
//        System.out.println(newFileManager.getTasks().size());
        System.out.println(newFileManager.getEpics().size());
        Task newTask = newFileManager.getTaskById(task1.getIdNum());
        Epic newEpic = newFileManager.getEpicByID(epic1.getIdNum());
        Subtask newSubtask = newFileManager.getSubtaskByID(subtask1.getIdNum());
        System.out.println(subtask1.getIdNum() + "is" + newSubtask.getIdNum());

        fileManager.deleteEpicById(epic1.getIdNum());
        System.out.println(fileManager.getEpics().size());
        System.out.println(fileManager.getEpics().get(epic2.getIdNum()).getTitle());
//        int i = 1;
//        System.out.println(1);

//        taskManager.createTask(task1);
//        taskManager.createTask(task2);
//
//        Epic epic1 = new Epic("Java", "learn coding");
//        taskManager.createEpic(epic1);
//
//        Subtask subtask1 = new Subtask("Master static modifier", "solve more problems", epic1.getIdNum());
//        Subtask subtask2 = new Subtask("Study OOP closer", "solve more problems", epic1.getIdNum());
//        Subtask subtask3 = new Subtask("string", "string class", epic1.getIdNum());
//        taskManager.createSubtask(subtask2);
//        taskManager.createSubtask(subtask1);
//        taskManager.createSubtask(subtask3);
//
//        Epic epic2 = new Epic("Efficient tutoring", "enhance st skills");
//        taskManager.createEpic(epic2);
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
//        taskManager.getSubtaskByID(subtask2.getIdNum());
//        taskManager.getSubtaskByID(subtask3.getIdNum());
//
//        System.out.println(historyManager.getHistory().size());
//
//        historyManager.remove(task1.getIdNum());
//        System.out.println(historyManager.getHistory().size());
//
//        taskManager.deleteEpicById(epic1.getIdNum());
//
//        System.out.println(historyManager.getHistory().size());

    }
}
