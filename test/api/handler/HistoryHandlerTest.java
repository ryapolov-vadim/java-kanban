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

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryHandlerTest {
    private final TaskManager manager = Managers.getDefault();
    private final HttpTaskServer taskServer  = new HttpTaskServer(8080, manager);
    private final Gson gson = HttpTaskServer.getGson();
    private DateTimeFormatter dtf;
    private HttpClient client;

    HistoryHandlerTest() throws IOException {
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
    void getYourBrowsingHistory() throws IOException, InterruptedException {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("03.01.2020 - 09:00", dtf));
        final int taskId = manager.createTask(task);
        Epic epic = new Epic("Test Epic", "Test DescriptionEpic");
        final int epicId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());
        final int subTaskId = manager.createSubTask(subTask);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        //Исполнение
        manager.findTaskById(taskId);
        manager.findEpicById(epicId);
        manager.findSubTaskById(subTaskId);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> taskList = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

        JsonObject jEpic = array.get(1).getAsJsonObject();
        Epic epic2 = new Epic(jEpic.get("name").getAsString(), jEpic.get("description").getAsString());
        epic2.setType(TaskType.valueOf(jEpic.get("type").getAsString()));
        epic2.setStatus(Status.valueOf(jEpic.get("status").getAsString()));
        epic2.setEndTime(LocalDateTime.parse(jEpic.get("endTime").getAsString(), dtf));
        epic2.setId(Integer.parseInt(jEpic.get("id").getAsString()));
        epic2.setDuration(Duration.parse(jEpic.get("duration").getAsString()));
        epic2.setStartTime(LocalDateTime.parse(jEpic.get("startTime").getAsString(), dtf));
        JsonArray subTaskIdArray = jEpic.getAsJsonArray("subTasksIds");
        for (JsonElement js : subTaskIdArray) {
            epic2.getSubTasksIds().add(js.getAsInt());
        }

        JsonObject jSubTaskOne = array.get(2).getAsJsonObject();
        SubTask subTaskOne = new SubTask(jSubTaskOne.get("name").getAsString(), jSubTaskOne.get("description").getAsString(),
                Status.valueOf(jSubTaskOne.get("status").getAsString()),
                Duration.parse(jSubTaskOne.get("duration").getAsString()),
                LocalDateTime.parse(jSubTaskOne.get("startTime").getAsString(), dtf),
                epic2.getId());
        subTaskOne.setType(TaskType.valueOf(jSubTaskOne.get("type").getAsString()));
        subTaskOne.setId(Integer.parseInt(jSubTaskOne.get("id").getAsString()));

        Epic epic1 = manager.findEpicById(epicId); //Эпик изменился из-за Сабтаска

        //Проверка
        assertEquals(200, response.statusCode());
        assertEquals(task, taskList.get(0), "Задачи не совпадают");
        assertEquals(taskList.size(), 3, "Неверное количество задач");
        assertEquals(epic1, epic2, "Эпики не совпадают");
        assertEquals(subTaskOne, subTask, "SubTask'и не совпадает");
    }
}