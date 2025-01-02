import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.manager.TaskManager;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task = new Task(null,"Сделать ТЗ", "Написать код", Status.NEW);
        Task createdTasc = taskManager.createTask(task);
        Integer createdTascId = createdTasc.getId();
        Task updatedTask = taskManager.updateTask(new Task(createdTascId, "Сделать ТЗ", "Исправить баги",
                Status.DONE));
        Task task1 = taskManager.createTask(new Task(null,"Отправить код",
                "Дописать код и отправить на проверку", Status.NEW));
        Integer task1Id = task1.getId();
        //удаление конкретной задачи
        //Task task1Delete = taskManager.deleteTask(task1Id);
        List<Task> findeAllTasks = taskManager.findAllTasks();
        //вывод всех Тасков
        System.out.println(findeAllTasks);
        System.out.println();


        Epic epic1 = taskManager.createEpic(new Epic(null, "Строить дом", "Список дел"));
        Integer createdEpic = epic1.getId();
        SubTask subTask = taskManager.createSubTask(new SubTask(null, "Фундамент",
                "Заливка фундамента", Status.NEW, createdEpic));
        Integer subtaskId = subTask.getId();
        SubTask subTask1 = taskManager.createSubTask(new SubTask(null, "Стены", "Купить блоки",
                Status.NEW, createdEpic));
        Integer sbTask1Id = subTask1.getId();
        SubTask subTaskUpdate = taskManager.updateSubTask(new SubTask(subtaskId, "Фундамент",
                "Заливка фундамента", Status.NEW, createdEpic));
        SubTask subTask1Update = taskManager.updateSubTask(new SubTask(sbTask1Id, "Стены",
                "Купить блоки", Status.IN_PROGRESS, createdEpic));

        List<SubTask> epicSubtask = taskManager.findAllEpicSubtasks(epic1);

        Epic epic2 = taskManager.createEpic(new Epic(null, "Сходить в поход", "Купить всё к походу"));
        Integer epic2Id = epic2.getId();
        SubTask subTask4 = taskManager.createSubTask(new SubTask(null, "Покупки", "Купить батон",
                Status.NEW, epic2Id));
        Integer subTask4Id = subTask4.getId();
        SubTask subTask4Update = taskManager.updateSubTask(new SubTask(subTask4Id, "Покупки",
                "Купить батон", Status.IN_PROGRESS, epic2Id));
        //удаление конкретного Эпика
        //Epic epic2Delete = taskManager.deleteEpic(epic2Id);
        List<Epic> epic = taskManager.findAllEpic();
        List<SubTask> epik2Subtask = taskManager.findAllEpicSubtasks(epic2);
        //вывести все эпики
        System.out.println(epic);
        System.out.println();

        //вывести все подзадачи определенных эпиков
        System.out.println(epicSubtask);
        System.out.println();
        //вывод подзадачи определенного Эпика
        System.out.println(epik2Subtask);
    }
}
