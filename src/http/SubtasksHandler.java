package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import model.Task;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SubtasksHandler(TaskManager taskManager) {
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

        if ("GET".equals(method) && "/subtasks".equals(path)) {
            handleGetSubtasks(exchange, taskManager);
        } else if ("GET".equals(method) && path.startsWith("/subtasks/")) {
            handleGetSubtaskById(exchange, taskManager);
        } else if ("POST".equals(method) && "/subtasks".equals(path)) {
            handlePostSubtask(exchange, taskManager);
        } else if ("DELETE".equals(method) && path.startsWith("/subtasks/")) {
            handleDeleteSubtask(exchange, taskManager);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение всех подзадач
        String response = gson.toJson(taskManager.getSubTasks());
        sendText(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение подзадачи по ID с использованием TaskManager
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int subTaskId = Integer.parseInt(pathParts[2]);
            SubTask subTask = taskManager.getSubTaskById(subTaskId);
            if (subTask == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(subTask);
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostSubtask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на создание или изменение подадачи  использованием TaskManager
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
        SubTask subTask;

        try {
            subTask = gson.fromJson(requestBody, SubTask.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (subTask == null) {
            sendNotFound(exchange);
            return;
        }

        Integer subTaskId = subTask.getId();
        if (subTaskId != null) {
            System.out.println("Updating subTask: " + subTaskId);
            taskManager.updateSubTask(subTask);
        } else {
            System.out.println("Creating new subTask: " + subTaskId);
            taskManager.createSubTask(subTask);
        }

        String response = gson.toJson(subTask);
        sendText(exchange, response);
    }

    private void handleDeleteSubtask(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на удаление подтзадачи по ID с использованием TaskManager
        System.out.println("delete");
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int subTaskId = Integer.parseInt(pathParts[2]);
            SubTask subTask = taskManager.getSubTaskById(subTaskId);
            System.out.println("Deleting subTask: " + subTaskId);
            System.out.println("Deleting subTaskSS: " + subTask);
            if (subTask == null) {
                sendNotFound(exchange);
            } else {
                System.out.println("remove" + subTask.getId());
                taskManager.deleteTaskById(subTask.getId());
                sendText(exchange, "Подзадача c ID: " + subTask.getId() + " удалена");
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}