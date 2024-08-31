package com.yandex.add;

import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Parent-teacher conf", "school year org");
        Task task2 = new Task("Shopping", "water filter");
        Epic epic1 = new Epic("Java", "learn coding");
        taskManager.createEpic(epic1);
        Subtask subtask1_1 = new Subtask("Master static modifier", "solve more problems", epic1.getIdNum());
        Subtask subtask1_2 = new Subtask("Study OOP closer", "solve more problems",  epic1.getIdNum());
        Epic epic2 = new Epic("Efficient tutoring", "enhance st skills");
        taskManager.createEpic(epic2);
        Subtask subtask2_1 = new Subtask("Master english", "practice more",  epic2.getIdNum());
        Subtask subtask2_2 = new Subtask("Master maths", "solve more math problems", epic2.getIdNum());

        taskManager.createTask(task1);
        taskManager.createSubtask(subtask1_2);
        taskManager.createSubtask(subtask1_1);

        taskManager.createTask(task2);
        taskManager.createSubtask(subtask2_1);
        taskManager.createSubtask(subtask2_2);
        taskManager.deleteTaskById(task2.getIdNum());
        taskManager.deleteSubtaskById(subtask2_2.getIdNum());

//       taskManager.deleteEpicById(epic2.getIdNum());

//        taskManager.getTaskById(task1.getIdNum());
//        taskManager.getSubtasksByEpic(epic1);
//        task1.setTitle("New title");
//        task1.setDescription("New description");
//        taskManager.updateTask(task1);

//        epic1.setTitle("New epic title");
//        epic1.setDescription("New epic description");
//        taskManager.updateEpic(epic1);

//        subtask1_1.setTitle("New subtask title");
//        subtask1_1.setDescription("New subtask description");
//        taskManager.updateSubtack(subtask1_1);

        taskManager.printTask();
        taskManager.printEpicsAndSubtasks(epic1.getIdNum());

//        taskManager.setEpicStatus(epic1);


    }
}
