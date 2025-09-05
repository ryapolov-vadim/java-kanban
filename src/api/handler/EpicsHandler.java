package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.Tasks.Epic;
import manager.Tasks.SubTask;
import manager.exception.*;
import manager.manager.TaskManager;
import manager.manager.TaskType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] patch = exchange.getRequestURI().getPath().split("/");
        try {
            switch (metod) {
                case "GET":
                    if (patch.length == 4) {
                        getEpicSubTasks(exchange);
                    } else if (patch.length == 3) {
                        getEpicById(exchange);
                    } else {
                        getAllEpic(exchange);
                    }
                    break;
                case "POST":
                    if (patch.length == 3) {
                        updateEpic(exchange);
                    } else {
                        createEpic(exchange);
                    }
                    break;
                case "DELETE":
                    if (patch.length == 3) {
                        deleteEpicId(exchange);
                    } else {
                        deleteAllEpic(exchange);
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

    private void getEpicSubTasks(HttpExchange exchange) throws IOException {
        List<SubTask> subTaskList;
        String[] url = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = url.length > 2 ? Optional.ofNullable(url[2]) : Optional.empty();
        String[] subTaskUrl = exchange.getRequestURI().getPath().split("/");
        Optional<String> optionalSub = subTaskUrl.length > 3 ? Optional.ofNullable(subTaskUrl[3]) : Optional.empty();
        int idEpic;
        try {
            idEpic = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }

        if (!optionalSub.get().equals("subtasks")) {
            String error = "Некорректный запрос клиента " + optionalSub.get();
            throw new ExceptionOfAnIncorrectRequest(error);
        }

        Epic epic = taskManager.findEpicById(idEpic);
        if (epic != null) {
            subTaskList = taskManager.findAllEpicSubtasks(epic);
            String json = gson.toJson(subTaskList);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Эпик с ID %d не найден", idEpic);
            throw new TaskNotFoundException(error);
        }
    }

    private void deleteAllEpic(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpic();
        sendText(exchange, "Задачи удалены", 200);
    }

    private void deleteEpicId(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }
        Epic epic = taskManager.deleteEpic(id);
        if (epic != null) {
            epic.setDescription("Задача успешно удалена");
            String json = gson.toJson(epic);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String epicJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicJson, Epic.class);
        epic.setType(TaskType.EPIC);
        taskManager.createEpic(epic);
        String json = gson.toJson(epic);
        sendText(exchange, json, 201);
    }

    private void updateEpic(HttpExchange exchange) throws IOException {
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
        String epicJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicJson, Epic.class);
        Epic epic2 = taskManager.updateEpic(id, epic);
        if (epic2 != null) {
            String json = gson.toJson(epic2);
            sendText(exchange, json, 201);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }

    private void getAllEpic(HttpExchange exchange) throws IOException {
        List<Epic> epicsList = taskManager.findAllEpic();
        String gsonTasks = gson.toJson(epicsList);
        sendText(exchange, gsonTasks, 200);
    }

    private void getEpicById(HttpExchange exchange) throws IOException {
        String[] idS = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = idS.length > 2 ? Optional.ofNullable(idS[2]) : Optional.empty();
        int id;
        try {
            id = Integer.parseInt(optional.get());
        } catch (NumberFormatException e) {
            String error = "Некорректный запрос клиента";
            throw new ExceptionOfAnIncorrectRequest(error);
        }

        Epic epic = taskManager.findEpicById(id);
        if (epic != null) {
            String json = gson.toJson(epic);
            sendText(exchange, json, 200);
        } else {
            String error = String.format("Задача с ID %d не найдена", id);
            throw new TaskNotFoundException(error);
        }
    }
}
