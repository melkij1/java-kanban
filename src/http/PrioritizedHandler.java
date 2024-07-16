package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public PrioritizedHandler(TaskManager taskManager) {
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
        if ("GET".equals(method) && "/prioritized".equals(path)) {
            handleGetPrioritized(exchange, taskManager);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение истории
        String response = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, response);
    }
}