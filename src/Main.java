import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.manager.FileBackedTaskManager;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File file = new File("backupFile\\backup.CSV");
        FileBackedTaskManager fBTaskManager = new FileBackedTaskManager(file);

        Task task = new Task("Task1", "Description Task1", Status.NEW);
        fBTaskManager.createTask(task);
        Task task2 = new Task("Task2", "Description Task2", Status.IN_PROGRESS);
        fBTaskManager.createTask(task2);
        Task task3 = new Task("Task3", "Description Task3", Status.DONE);
        fBTaskManager.createTask(task3);

        Epic epic = new Epic("Epic1", "Description Epic1");
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        fBTaskManager.createEpic(epic);
        fBTaskManager.createEpic(epic2);

        SubTask subTask = new SubTask("SubTask1", "Description SubTask1", Status.DONE, epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2", Status.NEW, epic.getId());
        SubTask subTask3 = new SubTask("SubTask3", "Description SubTask3", Status.IN_PROGRESS, epic2.getId());
        fBTaskManager.createSubTask(subTask);
        fBTaskManager.createSubTask(subTask2);
        fBTaskManager.createSubTask(subTask3);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = fileBackedTaskManager.findAllTasks();
        List<Epic> epics = fileBackedTaskManager.findAllEpic();
        List<SubTask> subTasks = fileBackedTaskManager.findAllSubTask();

        System.out.println("Вывод Задач");
        System.out.println(tasks);
        System.out.println();
        System.out.println("Вывод Эпиков");
        System.out.println(epics);
        System.out.println();
        System.out.println("Вывод Сабтасков");
        System.out.println(subTasks);
    }
}
