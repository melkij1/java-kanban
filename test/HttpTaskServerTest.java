import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enums.TaskStatus;
import http.HttpTaskServer;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    static HttpTaskServer httpTaskServer;
    static Gson gson;
    static TaskManager taskManager;
    static final LocalDateTime DATE = LocalDateTime.of(2024,1,1,0,0);
    static final LocalDateTime SUB_DATE = LocalDateTime.of(2024,3,10,0,0);

    private Task task;
    private Epic epic;
    private SubTask subTask;


    @BeforeEach
    void setUp() throws IOException {
        taskManager = new Managers().getTaskManager();
        httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.start();

        taskManager.removeAllTask();
        taskManager.removeAllEpic();
        taskManager.removeAllSubTask();

        task = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW, DATE, 1000);
        epic = new Epic("Эпик 1", "Описание эпика 1");
        subTask = new SubTask("Подзадача 1", "Описание подзадачи 1", 1, SUB_DATE, 1000);


        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubTaskById(2);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    void stopServer() {
        httpTaskServer.stop();
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(tasksList, "Список задач не получен");
        assertEquals(taskManager.getTasks(), tasksList, "Получен неверный список задач");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();

        List<Epic> epicList = gson.fromJson(response.body(), epicType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicList, "Список задач не получен");
        assertEquals(taskManager.getEpics(), epicList, "Получен неверный список задач");
    }

    @Test
    void getAllSubTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subTaskType = new TypeToken<List<SubTask>>() {
        }.getType();

        List<SubTask> subTaskList = gson.fromJson(response.body(), subTaskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subTaskList, "Список подзадач не получен");
        assertEquals(taskManager.getSubTasks(), subTaskList, "Получен неверный список подзадач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskDeserialized = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(taskDeserialized, "Задача не получена");
        assertEquals(taskManager.getTaskById(0), taskDeserialized, "Получена неверная задача");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicDeserialized = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicDeserialized, "Эпик не получена");
        assertEquals(taskManager.getEpicById(1), epicDeserialized, "Получен неверный эпик");
    }

    @Test
    void getSubTasksById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskDeserialized = gson.fromJson(response.body(), SubTask.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subTaskDeserialized, "Подзадача не получена");
        assertEquals(taskManager.getSubTaskById(2), subTaskDeserialized, "Получена неверная подзадача");
    }

    @Test
    void getTaskIncorrectId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не 404");
    }

    @Test
    void getEpicIncorrectId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics/4");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не 404");
    }

    @Test
    void getSubtasksIncorrectId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/subtasks/a");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не 404");
    }

    @Test
    void getSubtasksByOneEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<SubTask>>() {
        }.getType();
        List<SubTask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getListSubTaskByEpicId(1), subtasksList, "Получен неверный список подзадач");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(history, "Список истории не получен");
        assertEquals(3, history.size(), "Длина списка истории не 3");
        assertEquals(taskManager.getHistory().get(0).getId(), history.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(1).getId(), history.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(2).getId(), history.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> priority = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(priority, "Список приоритетных задач не получен");
        assertEquals(2, priority.size(), "Длина списка приоритетных задач не 3");
        assertEquals(taskManager.getPrioritizedTasks().get(0).getId(), priority.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(1).getId(), priority.get(1).getId(),
                "Id третьего элемента списка не совпадает");
    }


    @Test
    void addNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/tasks");
        Task task3 = new Task("Задача4", "description5", TaskStatus.NEW,
                LocalDateTime.of(2024, 6, 7, 10, 0), 1000);
        String json = gson.toJson(task3);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 201");
        assertEquals(3, taskManager.getTasks().get(1).getId(), "Id новой задачи не совпадает");
        assertEquals(2, taskManager.getTasks().size(), "Новая задача не добавлена");
    }

    @Test
    void addNewEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics");
        Epic epic2 = new Epic("новый Эпик 2", "Описание эпика 2");
        String json = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 201");
        assertEquals(3, taskManager.getEpics().get(1).getId(), "Id нового эпика не совпадает");
        assertEquals(2, taskManager.getEpics().size(), "Новый эпик не добавлен");
    }

    @Test
    void addNewSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/subtasks");
        SubTask subTask2 = new SubTask("Подзадача 3", "Описание подзадачи 3", 1,
                LocalDateTime.of(2024, 2, 10, 13, 0), 1000);
        String json = gson.toJson(subTask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 201");
        assertEquals(3, taskManager.getSubTasks().get(1).getId(), "Id нового эпика не совпадает");
        assertEquals(2, taskManager.getSubTasks().size(), "Новый эпик не добавлен");
    }

    @Test
    void updateNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/tasks");
        Task taskUpdate = new Task(0,"Задача 1 обновление", "Описание задачи 1 обновление", TaskStatus.NEW, DATE, 1000);

        String json = gson.toJson(taskUpdate);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode(), "Код ответа не 201");
        assertEquals(0, taskManager.getTasks().get(0).getId(), "Id обновленной задачи не совпадает");
    }
//
    @Test
    void updateNewEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics");
        Epic epic2 = new Epic(1, "Эпик 1 обновление", "Описание эпика 1 обновление", TaskStatus.NEW, DATE, 1000);
        String json = gson.toJson(epic2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 201");
        assertEquals(1, taskManager.getEpics().get(0).getId(), "Id нового эпика не совпадает");
    }

    @Test
    void updateNewSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/subtasks");
        SubTask subTask2 = new SubTask(2, "Подзадача 3", "Описание подзадачи 3", TaskStatus.IN_PROGRESS, 1,
                LocalDateTime.of(2024, 2, 10, 13, 0), 1000);
        String json = gson.toJson(subTask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 201");
        assertEquals(2, taskManager.getSubTasks().get(0).getId(), "Id обновленной подзадачи не совпадает");
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        Assertions.assertTrue(taskManager.getTasks().isEmpty(), "Задача не удалена");
        assertNull(taskManager.getTaskById(0), "Задача не удалена");
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://127.0.0.1:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        Assertions.assertTrue(taskManager.getEpics().isEmpty(), "Эпик не удален");
        assertNull(taskManager.getEpicById(1), "Эпик не удален");
    }

    @Test
    void removeSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        System.out.println(taskManager.getSubTasks());
        URI url = URI.create("http://127.0.0.1:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode(), "Код ответа не 200");
            Assertions.assertTrue(taskManager.getSubTasks().isEmpty(), "Подзадача не удалена");
            assertNull(taskManager.getSubTaskById(2), "Подзадача не удалена");
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("InterruptedException occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
