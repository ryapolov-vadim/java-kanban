package api.handler;

import api.HttpTaskServer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.manager.Managers;
import manager.manager.TaskManager;
import manager.manager.TaskType;
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

class PrioritizedHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer = new HttpTaskServer(8080, manager);
    private final Gson gson = HttpTaskServer.getGson();
    private DateTimeFormatter dtf;
    private HttpClient client;

    PrioritizedHandlerTest() throws IOException {
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
    void priorityTaskOutput() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("03.01.2020 - 09:00", dtf));
        final int taskId = manager.createTask(task);

        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        final int epicId = manager.createEpic(epic);

        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2025 - 09:00", dtf), epicId);
        final int subTaskId = manager.createSubTask(subTask);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        // Исполнение
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

        // Вытаскиваем SubTask из JSON
        JsonObject jSubTaskOne = array.get(1).getAsJsonObject();
        SubTask subTaskOne = new SubTask(jSubTaskOne.get("name").getAsString(),
                jSubTaskOne.get("description").getAsString(),
                Status.valueOf(jSubTaskOne.get("status").getAsString()),
                Duration.parse(jSubTaskOne.get("duration").getAsString()),
                LocalDateTime.parse(jSubTaskOne.get("startTime").getAsString(), dtf),
                epicId);
        subTaskOne.setType(TaskType.valueOf(jSubTaskOne.get("type").getAsString()));
        subTaskOne.setId(Integer.parseInt(jSubTaskOne.get("id").getAsString()));

        // Проверка
        assertTrue(taskList.contains(task), "В списке должен быть Task");
        assertEquals(subTaskOne, subTask, "SubTask'и не совпадает");
        assertEquals(2, taskList.size(), "Должно быть ровно 2 элемента");
    }
}