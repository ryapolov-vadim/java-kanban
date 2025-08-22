import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.manager.FileBackedTaskManager;
import manager.manager.Managers;
import manager.manager.TaskManager;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        File file = new File("backup.CSV");
        TaskManager taskManager = Managers.getDefault();
        FileBackedTaskManager fBTaskManager = new FileBackedTaskManager(file);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
//        Task task = new Task("Task1", "Description Task1", Status.NEW, Duration.ofMinutes(30),
//                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
//        int iii = fBTaskManager.createTask(task);
//        fBTaskManager.findTaskById(iii); //просмотренная задача
//        // Не имеет длительности поэтому не должна попасть в приоритет
//        Task task2 = new Task("Task2", "Description Task2", Status.IN_PROGRESS, null,
//                LocalDateTime.parse("01.01.2020 - 10:00", dtf));
//        int id2 = fBTaskManager.createTask(task2);
//        fBTaskManager.findTaskById(id2);
//        // пересекается с 1 задачей
//        Task task3 = new Task("Task3", "Description Task3", Status.DONE, Duration.ofMinutes(60),
//                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
//        fBTaskManager.createTask(task3);
//        // Обновление первой task1
//        Task updateTask = new Task("ТАСК1", "ЗАДАНИЕ ТАСК1", Status.DONE, Duration.ofMinutes(60),
//                LocalDateTime.parse("01.01.2020 - 08:00", dtf));
//        fBTaskManager.updateTask(iii, updateTask);
//
//        Task task4 = new Task("Task4", "Description Task4", Status.DONE, Duration.ofMinutes(30),
//                LocalDateTime.parse("01.01.2020 - 09:30", dtf));
//        int id4 = fBTaskManager.createTask(task4);
//        fBTaskManager.findTaskById(id4);
//        Task task5 = new Task("Task5", "Description Task5", Status.DONE, Duration.ofMinutes(30),
//                LocalDateTime.parse("01.01.2020 - 10:00", dtf));
//        fBTaskManager.createTask(task5);
//        Task task6 = new Task("Task6", "Description Task6", Status.DONE, Duration.ofMinutes(30),
//                LocalDateTime.parse("01.01.2020 - 10:30", dtf));
//        fBTaskManager.createTask(task6);

        // удаление задач Task
        //fBTaskManager.deleteTask(iii);
        //fBTaskManager.deleteAllTask();

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

        List<Epic> epicsdsff = fBTaskManager.findAllEpic();
        List<SubTask> subTasksfdsf = fBTaskManager.findAllSubTask();

//        удаление конкретного сабтаска
//        fBTaskManager.deleteSubTask(subTask5.getId());
//
        //удаление всех эпиков
        //fBTaskManager.deleteAllEpic();
//         удаление конкретного эпика
//        fBTaskManager.deleteEpic(epic2.getId());

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasks = fileBackedTaskManager.findAllTasks();
        List<Epic> epics = fileBackedTaskManager.findAllEpic();
        List<SubTask> subTasks = fileBackedTaskManager.findAllSubTask();

        List<Task> taskList = fBTaskManager.getPrioritizedTasks();
        List<Task> taskListHistory = fBTaskManager.getHistory();

        System.out.println("Вывод Задач");
        System.out.println(tasks);
        System.out.println();
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
