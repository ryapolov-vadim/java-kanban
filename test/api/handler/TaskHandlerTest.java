package api.handler;

import api.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Tasks.Status;
import manager.Tasks.Task;
import manager.manager.Managers;
import manager.manager.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer = new HttpTaskServer(8080, manager);
    private final Gson gson = HttpTaskServer.getGson();
    private DateTimeFormatter dtf;
    private HttpClient client;

    TaskHandlerTest() throws IOException {
    }

    @BeforeEach
    void setUp() {
        manager.deleteAllTask();
        manager.deleteAllSubTask();
        manager.deleteAllEpic();
        dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
        client = HttpClient.newHttpClient();
        taskServer.start();

    }

    @AfterEach
    void shutDown() {
        taskServer.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = manager.findAllTasks();

        //Проверка
        assertEquals(201, response.statusCode(), "Неверный код");
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size());
    }

    @Test
    void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Task task2 = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf));
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String taskString = response.body();
        Task task3 = gson.fromJson(taskString, Task.class);

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(task, task3, "Задачи не совпадают");
    }

    @Test
    void testToReturnAllTasks() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Task task2 = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf));
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String tasks = response.body();
        List<Task> taskList = gson.fromJson(tasks, new TypeToken<List<Task>>() {
        }.getType());

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(task, taskList.get(0), "Задачи не совпадают");
        assertEquals(task2, taskList.get(1), "Задачи не совпадают");
    }

    @Test
    void UpdateATaskByID() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        //Меняю Duration и LocalDateTime
        Task taskUpdate = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("10.01.2025 - 10:00", dtf));
        String update = gson.toJson(taskUpdate);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(update))
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task newUpdateTask = manager.findTaskById(task.getId());

        //Проверка
        assertEquals(201, response.statusCode(), "Неверный код");
        assertEquals(newUpdateTask.getDuration(), taskUpdate.getDuration(), "Duration не совпадают");
        assertEquals(newUpdateTask.getEndTime(), taskUpdate.getEndTime(), "EndTime не совпадают");
        assertEquals(newUpdateTask.getStartTime(), taskUpdate.getStartTime(), "StartTime не совпадают");
    }

    @Test
    void DeleteAnTaskByID() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Task task2 = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf));
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = manager.findAllTasks();

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(task, taskList.get(0), "Задачи не совпадают");
        assertEquals(1, taskList.size(), "Вернулось неверное количество задач");
    }

    @Test
    void deleteAllTask() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Task task2 = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf));
        manager.createTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = manager.findAllTasks();

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertTrue(taskList.isEmpty(), "Список не пуст");
    }
}