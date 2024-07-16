package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        handle(exchange, this.taskManager);
    }

    @Override
    public void handle(HttpExchange exchange, TaskManager taskManager) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        System.out.println("Received request: " + method + " " + path);
        if ("GET".equals(method) && "/tasks".equals(path)) {
            handleGetTasks(exchange, taskManager);
        } else if ("GET".equals(method) && path.startsWith("/tasks/")) {
            handleGetTaskById(exchange, taskManager);
        } else if ("POST".equals(method) && "/tasks".equals(path)) {
            handlePostTask(exchange, taskManager);
        } else if ("DELETE".equals(method) && path.startsWith("/tasks/")) {
            handleDeleteTask(exchange, taskManager);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetTasks(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение всех задач
        String response = gson.toJson(taskManager.getTasks());
        sendText(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение задачи по ID с использованием TaskManager
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int taskId = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTaskById(taskId);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(task);
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на создание или изменение Задачи  использованием TaskManager
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendNotFound(exchange);
            return;
        }

        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        Task task;

        try {
            task = gson.fromJson(requestBody, Task.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        Integer taskId = task.getId();
        if (taskId != null) {
            System.out.println("Updating task: " + taskId);
            taskManager.updateTask(task);
        } else {
            System.out.println("Creating new task: " + taskId);
            taskManager.createTask(task);
        }

        String response = gson.toJson(task);
        sendText(exchange, response);
    }

    private void handleDeleteTask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на удаление задачи по ID с использованием TaskManager
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int taskId = Integer.parseInt(pathParts[2]);
            Task task = taskManager.getTaskById(taskId);
            if (task == null) {
                sendNotFound(exchange);
            } else {
                taskManager.deleteTaskById(task.getId());
                sendText(exchange, "Задача c ID: " + task.getId() + " удалена");
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}