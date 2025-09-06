package api.handler;

import api.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Tasks.Epic;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer = new HttpTaskServer(8080, manager);
    private final Gson gson = HttpTaskServer.getGson();
    private DateTimeFormatter dtf;
    private HttpClient client;

    EpicsHandlerTest() throws IOException {
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
    void testAddEpic() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        String json = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = manager.findAllEpic();

        //Проверка
        assertEquals(201, response.statusCode(), "Неверный код");
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size());
    }

    @Test
    void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        //Получаем ID
        final int id = manager.createEpic(epic);
        String idSt = String.valueOf(id);

        URI url = URI.create("http://localhost:8080/epics/" + idSt);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String epicString = response.body();
        Epic epicNew = gson.fromJson(epicString, Epic.class);

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(epic, epicNew, "Подзадачи не совпадают");
    }

    @Test
    void testToReturnAllSubTasks() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Description 2");
        Epic epic3 = new Epic("Test Epic 3", "Test Description 3");
        manager.createEpic(epic);
        manager.createEpic(epic2);
        manager.createEpic(epic3);

        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String epics = response.body();
        List<Epic> taskList = gson.fromJson(epics, new TypeToken<List<Epic>>() {
        }.getType());

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(3, taskList.size(), "Неверное количество подзадач");
        assertEquals(epic, taskList.get(0), "Задачи не совпадают");
        assertEquals(epic2, taskList.get(1), "Задачи не совпадают");
        assertEquals(epic3, taskList.get(2), "Задачи не совпадают");
    }

    @Test
    void UpdateASubTaskByID() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Description 2");
        Epic epic3 = new Epic("Test Epic 3", "Test Description 3");
        final int id = manager.createEpic(epic);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        String idsb = String.valueOf(id);

        Epic epicUpdate = new Epic("Test epicUpdate", "DescriptionUpdate");
        String json = gson.toJson(epicUpdate);

        URI url = URI.create("http://localhost:8080/epics/" + idsb);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic newUpdateEpic = manager.findEpicById(id);

        //Проверка
        assertEquals(201, response.statusCode(), "Неверный код");
        assertEquals(epicUpdate.getName(), newUpdateEpic.getName(), "Name не совпадают");
        assertEquals(epicUpdate.getDescription(),
                newUpdateEpic.getDescription(), "Description не совпадают");
    }

    @Test
    void DeleteAnEpicByID() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Description 2");
        Epic epic3 = new Epic("Test Epic 3", "Test Description 3");
        final int id = manager.createEpic(epic);
        manager.createEpic(epic2);
        manager.createEpic(epic3);
        String idsb = String.valueOf(id);

        URI url = URI.create("http://localhost:8080/epics/" + idsb);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicList = manager.findAllEpic();
        Epic epicDelete = manager.findEpicById(id);

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertEquals(epic2, epicList.get(0), "Задачи не совпадают");
        assertEquals(epic3, epicList.get(1), "Задачи не совпадают");
        assertEquals(2, epicList.size(), "Вернулось неверное количество задач");
        assertNull(epicDelete, "Подзадача не удалена");
    }

    @Test
    void deleteAllSubTask() throws IOException, InterruptedException {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        Epic epic2 = new Epic("Test Epic 2", "Test Description 2");
        Epic epic3 = new Epic("Test Epic 3", "Test Description 3");
        manager.createEpic(epic);
        manager.createEpic(epic2);
        manager.createEpic(epic3);

        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        //Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicList = manager.findAllEpic();

        //Проверка
        assertEquals(200, response.statusCode(), "Неверный код");
        assertTrue(epicList.isEmpty(), "Список не пуст");
    }
}