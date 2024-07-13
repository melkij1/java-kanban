package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.IOException;
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
        System.out.println(gson.toJson(taskManager.getTasks()));
        if ("GET".equals(method) && "/tasks".equals(path)) {
            handleGetTasks(exchange, taskManager);
        } else if ("GET".equals(method) && path.startsWith("/tasks/")) {
            handleGetTaskById(exchange, taskManager);
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
}