package com.yandex.add.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.add.model.Epic;
import com.yandex.add.model.Subtask;
import com.yandex.add.model.Task;
import com.yandex.add.service.taskmanager.InMemoryTaskManager;
import com.yandex.add.service.taskmanager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTest {

    private TaskManager manager = new InMemoryTaskManager();
    private HttpTaskServer taskServer = new HttpTaskServer(manager);
    private Gson gson = HttpTaskServer.getGson();
    private HttpClient client;

    public HttpTaskManagerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2");
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", taskJson);
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    void shouldReturnTaskList() throws IOException, InterruptedException {
        manager.createTask(new Task("Test 2", "Testing task 2"));
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks");
        assertEquals(200, response.statusCode());
        final List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(tasks);
        assertEquals(manager.getTasks().size(), tasks.size());
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("task", "task"));
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/" + task.getId());
        assertEquals(200, response.statusCode());
        List<Task> tasks = manager.getTasks();
        assertEquals(0, tasks.size());

    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("task", "task"));
        HttpResponse response = sendGetRequest("http://localhost:8080/tasks/" + task.getId());
        assertEquals(200, response.statusCode());
        Task jsonTask = gson.fromJson(response.body().toString(), Task.class);
        assertNotNull(jsonTask);
        assertEquals(task.getTitle(), jsonTask.getTitle());
        assertEquals(task.getId(), jsonTask.getId());
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("task", "task"));
        String jsonTask = gson.toJson(task);
        HttpResponse response = sendPostRequest("http://localhost:8080/tasks/" + task.getId(), jsonTask);
        assertEquals(200, response.statusCode());

        Task fromJson = gson.fromJson(response.body().toString(), Task.class);
        assertEquals(task.getId(), fromJson.getId());
        assertEquals(task.getTitle(), fromJson.getTitle());

    }

    @Test
    void testNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/nay");
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldGetEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic("epic1", "epic1"));
        manager.createEpic(new Epic("epic2", "epic2"));
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/");
        assertEquals(200, response.statusCode());

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        assertNotNull(epics);
        assertEquals(2, epics.size());
    }

    @Test
    void shouldGetSubtasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("epic1", "epic1"));
        manager.createSubtask(new Subtask("subtask1", "subtask1", epic.getId()));
        manager.createSubtask(new Subtask("subtask2", "subtask2", epic.getId()));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/");
        assertEquals(200, response.statusCode());

        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        assertNotNull(subtasks);
        assertEquals(2, subtasks.size()); // Проверяем, что возвращены оба сабтаска
    }

    @Test
    void shouldGetHistory() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("epic1", "epic1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("subtask1", "subtask1", epic.getId()));
        Task task1 = manager.createTask(new Task("task1", "task1"));
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic.getId());
        manager.getSubtaskByID(subtask1.getId());

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/history/");
        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(history);
        assertEquals(history.size(), 3);
    }

    @Test
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = manager.createTask(new Task("task1", "task1"));
        Task task2 = manager.createTask(new Task("task2", "task2"));
        Epic epic = manager.createEpic(new Epic("epic1", "epic1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("subtask1", "subtask1", epic.getId()));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/prioritized");
        assertEquals(200, response.statusCode());
        List<Task> priorityTasks = gson.fromJson(response.body().toString(), new TypeToken<ArrayList<Task>>() {
        }.getType());
        assertNotNull(priorityTasks);
    }

    private HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(String url, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}