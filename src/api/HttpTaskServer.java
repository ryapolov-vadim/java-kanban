package api;

import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import api.handler.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.manager.Managers;
import manager.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private final int port;
    private final HttpServer server;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpTaskServer(int port, TaskManager taskManager) throws IOException {
        this.port = port;

        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на порту " + port);
        server.start();
    }

    public void stop() {
        System.out.println("HTTP-сервер остановлен");
        server.stop(0);
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer httpServer = new HttpTaskServer(8080, manager);
            httpServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка при запуске сервера " + e.getMessage());
        }
    }
}
