import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.manager.FileBackedTaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File file = new File("backup.CSV");
        FileBackedTaskManager fBTaskManager = new FileBackedTaskManager(file);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

        Epic epic = new Epic("Epic1", "Description Epic1");
        fBTaskManager.createEpic(epic);

        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.parse("01.01.2020 - 09:30", dtf), epic.getId());
        SubTask subTask3 = new SubTask("SubTask3", "Description SubTask3", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.parse("01.01.2020 - 10:00", dtf), epic.getId());
        SubTask subTask4 = new SubTask("СабТаскОбновленние", "Description Сабтаск2", Status.NEW,
                Duration.ofMinutes(30), null, epic.getId());

        fBTaskManager.createSubTask(subTask1);
        fBTaskManager.createSubTask(subTask2);
        fBTaskManager.createSubTask(subTask3);
        fBTaskManager.updateSubTask(subTask2.getId(), subTask4);

        fBTaskManager.findSubTaskById(subTask1.getId());
        fBTaskManager.findSubTaskById(subTask2.getId());
        fBTaskManager.findSubTaskById(subTask3.getId());

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        List<Epic> epics = fileBackedTaskManager.findAllEpic();
        List<SubTask> subTasks = fileBackedTaskManager.findAllSubTask();

        List<Task> taskList = fBTaskManager.getPrioritizedTasks();
        List<Task> taskListHistory = fBTaskManager.getHistory();

        System.out.println("Вывод Эпиков");
        System.out.println(epics);
        System.out.println();
        System.out.println("Вывод Сабтасков");
        System.out.println(subTasks);
        System.out.println();
        System.out.println("Вывод приоритетных задач");
        System.out.println(taskList);
        System.out.println();
        System.out.println("История");
        System.out.println(taskListHistory);
    }
}
