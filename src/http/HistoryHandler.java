package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HistoryHandler(TaskManager taskManager) {
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
        if ("GET".equals(method) && "/history".equals(path)) {
            handleGetHistory(exchange, taskManager);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange, TaskManager taskManager) throws IOException {
        // Логика для обработки запроса на получение истории
        String response = gson.toJson(taskManager.getHistory());
        sendText(exchange, response);
    }
}