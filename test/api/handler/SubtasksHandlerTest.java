package api.handler;

import api.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.manager.Managers;
import manager.manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

class SubtasksHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer  = new HttpTaskServer(8080, manager);
    private final Gson gson = HttpTaskServer.getGson();
    private DateTimeFormatter dtf;
    private HttpClient client;

    SubtasksHandlerTest() throws IOException {
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
    void testAddSubTask() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());

        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());

        String subTaskJson = gson.toJson(subTask);
        String subTask2Json = gson.toJson(subTask2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTask2Json))
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTasks = manager.findAllSubTask();

        //Проверка
        assertEquals(201, response.statusCode(), "Неверный код");
        assertEquals(201, response2.statusCode(), "Неверный код");
        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(2, subTasks.size());
    }

    @Test
    void testGetSubTaskByIdNotFound() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());
        //Получаем ID
        final int id = manager.createSubTask(subTask2);
        String idSt = String.valueOf(id);

        URI url = URI.create("http://localhost:8080/subtasks/" + idSt);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String taskString = response.body();
        SubTask subTaskNew = gson.fromJson(taskString, SubTask.class);

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(subTask2, subTaskNew, "Подзадачи не совпадают");
    }

    @Test
    void testToReturnAllSubTasks() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Test SubTask 3", "SubTask Description 3",
                Status.IN_PROGRESS, epickId);
        manager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Test SubTask 4", "SubTaskDescription 4", Status.NEW, epickId);
        manager.createSubTask(subTask4);

        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String subTasks = response.body();
        List<SubTask> taskList = gson.fromJson(subTasks, new TypeToken<List<SubTask>>(){}.getType());

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(4, taskList.size(), "Неверное количество подзадач");
        assertEquals(subTask, taskList.get(0), "Задачи не совпадают");
        assertEquals(subTask2, taskList.get(1), "Задачи не совпадают");
        assertEquals(subTask3, taskList.get(2), "Задачи не совпадают");
        assertEquals(subTask4, taskList.get(3), "Задачи не совпадают");
    }

    @Test
    void UpdateASubTaskByID() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());
        final int id = manager.createSubTask(subTask);
        String idsb = String.valueOf(id);

        SubTask subTaskUpdate = new SubTask("Test SubTaskUpdate 2", "SubTask DescriptionUpdate 2",
                Status.NEW,Duration.ofMinutes(50),
                LocalDateTime.parse("10.01.2020 - 10:00", dtf), epic.getId());
        String json = gson.toJson(subTaskUpdate);

        URI url = URI.create("http://localhost:8080/subtasks/" + idsb);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask newUpdateSubTask = manager.findSubTaskById(id);

        //Проверка
        assertEquals(201, response.statusCode(), "Неверный код");
        assertEquals(subTaskUpdate.getName(), newUpdateSubTask.getName(), "Name не совпадают");
        assertEquals(subTaskUpdate.getDescription(),
                newUpdateSubTask.getDescription(), "Description не совпадают");
        assertEquals(subTaskUpdate.getDuration(), newUpdateSubTask.getDuration(), "Duration не совпадают");
        assertEquals(subTaskUpdate.getEndTime(), newUpdateSubTask.getEndTime(), "EndTime не совпадают");
        assertEquals(subTaskUpdate.getStartTime(), newUpdateSubTask.getStartTime(), "StartTime не совпадают");
    }

    @Test
    void DeleteAnSubTaskByID() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        final int id = manager.createSubTask(subTask2);
        String idsb = String.valueOf(id);

        URI url = URI.create("http://localhost:8080/subtasks/" + idsb);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTaskList = manager.findAllSubTask();
        SubTask subTask1 = manager.findSubTaskById(id);

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(subTask, subTaskList.get(0), "Задачи не совпадают");
        assertEquals(1, subTaskList.size(), "Вернулось неверное количество задач");
        assertNull(subTask1, "Подзадача не удалена");
    }

    @Test
    void deleteAllSubTask() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Test SubTask 3", "SubTask Description 3",
                Status.IN_PROGRESS, epickId);
        manager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Test SubTask 4", "SubTaskDescription 4", Status.NEW, epickId);
        manager.createSubTask(subTask4);

        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> subTaskList = manager.findAllSubTask();

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertTrue(subTaskList.isEmpty(), "Список не пуст");
    }
}