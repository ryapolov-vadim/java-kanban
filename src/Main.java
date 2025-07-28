import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.manager.Managers;
import manager.manager.TaskManager;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        Task task = new Task("Task1", "Task1", Status.NEW);
        manager.createTask(task);
        manager.findTaskById(task.getId());
        manager.updateTask(task.getId(), new Task("Таск1", "Таск1", Status.IN_PROGRESS));
        manager.findTaskById(task.getId());
        //manager.deleteAllTask();

        Epic epic = new Epic("Epic1", "Epic1");
        Epic epic1 = new Epic("Epic2", "Epic2");
        manager.createEpic(epic);
        manager.createEpic(epic1);
        manager.findEpicById(epic.getId());
        manager.updateEpic(epic.getId(), new Epic("Эпик1", "Эпик1"));
        manager.findEpicById(epic.getId());
        //manager.deleteAllEpic();

        SubTask subTask = new SubTask("SubTask1", "SubTask1", Status.NEW, epic.getId());
        manager.createSubTask(subTask);
        manager.findSubTaskById(subTask.getId());
        manager.updateSubTask(subTask.getId(), new SubTask("Сабтаск1", "Сабтаск1",
                Status.DONE, epic.getId()));
        SubTask subTask1 = new SubTask("SubTask2", "SubTask2", Status.IN_PROGRESS, epic.getId());
        manager.createSubTask(subTask1);
        manager.findSubTaskById(subTask1.getId());
        //manager.deleteEpic(epic.getId());
        //manager.deleteAllEpic();
        //manager.deleteSubTask(subTask1.getId());

        List<Task> tasks = manager.findAllTasks();
        List<Epic> epics = manager.findAllEpic();
        List<SubTask> subTasks = manager.findAllSubTask();

        System.out.println("вывод задачь");
        System.out.println(tasks);
        System.out.println();
        System.out.println("вывод эпиков");
        System.out.println(epics);
        System.out.println();
        System.out.println("вывод подзадач");
        System.out.println(subTasks);
        System.out.println();
        System.out.println("Вывод истории");
        List<Task> history = manager.getHistory();
        System.out.println(history);
        System.out.println();
        System.out.println("вывод подзадач определённого Эпика");
        List<SubTask> subTasks1 = manager.findAllEpicSubtasks(epic);
        System.out.println(subTasks1);
        System.out.println();
    }
}
