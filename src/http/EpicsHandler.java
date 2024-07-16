package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.SubTask;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicsHandler(TaskManager taskManager) {
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
        if ("GET".equals(method) && "/epics".equals(path)) {
            handleGetEpics(exchange, taskManager);
        } else if ("GET".equals(method) && path.startsWith("/epics/") && !path.endsWith("/subtasks")) {
            handleGetEpicById(exchange, taskManager);
        } else if ("GET".equals(method) && path.endsWith("/subtasks")) {
            handleGetSubtaskByEpicId(exchange, taskManager);
        } else if ("POST".equals(method) && "/epics".equals(path)) {
            handlePostEpic(exchange, taskManager);
        } else if ("DELETE".equals(method) && path.startsWith("/epics/")) {
            handleDeleteEpic(exchange, taskManager);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение всех эпиков
        String response = gson.toJson(taskManager.getEpics());
        sendText(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение Эпика по ID с использованием TaskManager
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int epicId = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(epicId);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(epic);
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetSubtaskByEpicId(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение Эпика по ID с использованием TaskManager
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 4) {
            sendNotFound(exchange);
            return;
        }

        try {
            int epicId = Integer.parseInt(pathParts[2]);
            List<SubTask> subTasks = taskManager.getListSubTaskByEpicId(epicId);
            if (subTasks == null) {
                sendNotFound(exchange);
            } else {
                String response = gson.toJson(subTasks);
                sendText(exchange, response);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на создание или изменение Эпиков  использованием TaskManager
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
        Epic epic;

        try {
            epic = gson.fromJson(requestBody, Epic.class);
        } catch (JsonSyntaxException e) {
            sendNotFound(exchange);
            return;
        }

        if (epic == null) {
            sendNotFound(exchange);
            return;
        }

        Integer epicId = epic.getId();
        if (epicId != null) {
            taskManager.updateEpic(epic);
        } else {
            taskManager.createEpic(epic);
        }

        String response = gson.toJson(epic);
        sendText(exchange, response);
    }

    private void handleDeleteEpic(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на удаление Эпика по ID с использованием TaskManager
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length != 3) {
            sendNotFound(exchange);
            return;
        }

        try {
            int epicId = Integer.parseInt(pathParts[2]);
            Epic epic = taskManager.getEpicById(epicId);
            if (epic == null) {
                sendNotFound(exchange);
            } else {
                taskManager.deleteEpicById(epic.getId());
                sendText(exchange, "Эпик c ID: " + epic.getId() + " удалена");
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }
}