package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.Tasks.Task;
import manager.exception.*;
import manager.manager.TaskManager;
import manager.manager.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] patch = exchange.getRequestURI().getPath().split("/");
        try {
            switch (metod) {
                case "GET":
                    if (patch.length == 3) {
                        getTaskById(exchange);
                    } else {
                        getAllTasks(exchange);
                    }
                    break;
                case "POST":
                    if (patch.length == 3) {
                        updateTask(exchange);
                    } else {
                        createTask(exchange);
                    }
                    break;
                case "DELETE":
                    if (patch.length == 3) {
                        deleteTaskId(exchange);
                    } else {
                        deleteAllTasks(exchange);
                    }
                    break;
                default:
                    defaultBlock();
            }
        } catch (TimeVerificationException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 406);
            String json = gson.toJson(errorResponse);
            sendHasInteractions(exchange, json, errorResponse.getErroreCode());
        } catch (ExceptionOfAnIncorrectRequest e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 400);
            String json = gson.toJson(errorResponse);
            requestWasSentIncorrectly(exchange, json, errorResponse.getErroreCode());
        } catch (TaskNotFoundException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 404);
            String json = gson.toJson(errorResponse);
            sendNotFound(exchange, json, errorResponse.getErroreCode());
        } catch (ExceptionIsUnhandledMethods e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 405);
            String jrror = gson.toJson(errorResponse);
            requestWasSentIncorrectly(exchange, jrror, errorResponse.getErroreCode());
        } finally {
            exchange.close();
        }
    }

    private void defaultBlock() {
        String error = "Этот метод пока не обработан";
        throw new ExceptionIsUnhandledMethods(error);
    }

    private void deleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTask();
        sendText(exchange, "Задачи удалены", 200);
    }

    private void deleteTaskId(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }
        Task task = taskManager.deleteTask(id);
        if (task != null) {
            task.setDescription("Задача успешно удалена");
            String json = gson.toJson(task);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }

    private void createTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(taskJson, Task.class);
        task.setType(TaskType.TASK);
        taskManager.createTask(task);
        String json = gson.toJson(task);
        sendText(exchange, json, 201);
    }

    private void updateTask(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }
        InputStream is = exchange.getRequestBody();
        String taskJ = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(taskJ, Task.class);
        Task task2 = taskManager.updateTask(id, task);
        if (task2 != null) {
            String json = gson.toJson(task2);
            sendText(exchange, json, 201);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }

    private void getAllTasks(HttpExchange exchange) throws IOException {
        List<Task> taskList = taskManager.findAllTasks();
        String gsonTasks = gson.toJson(taskList);
        sendText(exchange, gsonTasks, 200);
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }

        Task task = taskManager.findTaskById(id);
        if (task != null) {
            String json = gson.toJson(task);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }
}
