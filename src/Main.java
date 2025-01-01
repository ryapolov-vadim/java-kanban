import manager.Tasks.Status;
import manager.Tasks.Task;
import manager.manager.TaskManager;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

    TaskManager taskManager = new TaskManager();

    Task task = new Task(null, "Сделать ТЗ", "Написать код", Status.NEW);
    Task createdTasc = taskManager.createTask(task);
    Integer createdTascId = createdTasc.getId();
    Task updatedTask = new Task(createdTascId, "Сделать ТЗ", "Исправить баги", Status.DONE);
    Integer createdTascId2 = createdTasc.getId();
    ArrayList<Task> findeAllTasks = taskManager.findeAllTasks();
    Task update = taskManager.updateTask(updatedTask);
    Task deleted = taskManager.deleteTask(createdTascId);
    Task findTaskById = taskManager.findTaskById(createdTascId2);
    //

}
}
