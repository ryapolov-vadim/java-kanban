package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.Tasks.Task;
import manager.exception.*;
import manager.manager.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metod = exchange.getRequestMethod();
        String[] patch = exchange.getRequestURI().getPath().split("/");
        Optional<String> optional = patch.length > 1 ? Optional.ofNullable(patch[1]) : Optional.empty();
        try {
            switch (metod) {
                case "GET":
                    if (optional.get().equals("history")) {
                        getHistory(exchange);
                    } else {
                        String error = "Некорректный запрос клиента";
                        throw new ExceptionOfAnIncorrectRequest(error);
                    }
                    break;
                default:
                    defaultBlock();
            }
        } catch (ExceptionOfAnIncorrectRequest e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), 400);
            String json = gson.toJson(errorResponse);
            requestWasSentIncorrectly(exchange, json, errorResponse.getErroreCode());
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

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> taskList = taskManager.getHistory();
        String json = gson.toJson(taskList);
        sendText(exchange, json, 200);
    }
}
