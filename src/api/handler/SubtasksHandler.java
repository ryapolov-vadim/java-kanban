package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.Tasks.SubTask;
import manager.exception.*;
import manager.manager.TaskManager;
import manager.manager.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
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
                        getSubTaskById(exchange);
                    } else {
                        getAllSubTasks(exchange);
                    }
                    break;
                case "POST":
                    if (patch.length == 3) {
                        updateSubTask(exchange);
                    } else {
                        createSubTask(exchange);
                    }
                    break;
                case "DELETE":
                    if (patch.length == 3) {
                        deleteSubTaskId(exchange);
                    } else {
                        deleteAllSubTasks(exchange);
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

    private void deleteAllSubTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubTask();
        sendText(exchange, "Задачи удалены", 200);
    }

    private void deleteSubTaskId(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }
        SubTask subTask = taskManager.deleteSubTask(id);
        if (subTask != null) {
            subTask.setDescription("Задача успешно удалена");
            String json = gson.toJson(subTask);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }

    private void createSubTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        SubTask task = gson.fromJson(taskJson, SubTask.class);
        task.setType(TaskType.SUBTASK);
        taskManager.createSubTask(task);
        String json = gson.toJson(task);
        sendText(exchange, json, 201);
    }

    private void updateSubTask(HttpExchange exchange) throws IOException {
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
        SubTask task = gson.fromJson(taskJ, SubTask.class);
        SubTask task2 = taskManager.updateSubTask(id, task);
        if (task2 != null) {
            String json = gson.toJson(task2);
            sendText(exchange, json, 201);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }

    private void getAllSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> taskList = taskManager.findAllSubTask();
        String gsonTasks = gson.toJson(taskList);
        sendText(exchange, gsonTasks, 200);
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }

        SubTask task = taskManager.findSubTaskById(id);
        if (task != null) {
            String json = gson.toJson(task);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }
}
