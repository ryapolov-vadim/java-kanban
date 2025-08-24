package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractTaskManagerTest <T extends  TaskManager> {
    protected T manager;
    protected DateTimeFormatter dtf;

    @BeforeEach
    void setupClass() {
        manager = getTaskManager();
        dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
    }

    abstract T getTaskManager();

    @Test
    void taskManagerInit() {
        assertNotNull(manager, "ошибка создания менеджера");
    }

    @Test
    void addNewTask() {
        // Подготовка
        Task task = new Task("Test Task", "Test description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        final int taskId = manager.createTask(task);

        // Исполнение
        final Task savedTask = manager.findTaskById(taskId);

        // Проверка
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        // Исполнение
        final List<Task> tasks = manager.findAllTasks();

        // Проверка
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test description");
        final int epicId = manager.createEpic(epic);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epicId);

        // Проверка
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        // Исполнение
        final List<Epic> epics = manager.findAllEpic();

        // Проверка
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewSubTask() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test descriptionEpic");
        final int epicId = manager.createEpic(epic);

        // Подготовка
        SubTask subTask = new SubTask("Test SubTask", "Test descriptionSubTask",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epicId);
        final int subTaskId = manager.createSubTask(subTask);

        // Исполнение
        final SubTask subTasks = manager.findSubTaskById(subTaskId);

        // Проверка
        assertNotNull(subTasks, "Задача не найдена.");
        assertEquals(subTasks, subTask, "Задачи не совпадают.");

        // Исполнение
        final List<SubTask> subTasks1 = manager.findAllSubTask();

        // Проверка
        assertNotNull(subTasks1, "Задачи не возвращаются.");
        assertEquals(1, subTasks1.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks1.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteAllTaskTest() {
        // Подготовка
        final int counter = 9;
        for (int i = 1; i <= counter; i++) {
            Task task = new Task("Test Task", "Test Description", Status.NEW, Duration.ofMinutes(30),
                    LocalDateTime.parse("0" + String.valueOf(i) + ".01.2020 - 09:00", dtf));
            manager.createTask(task);
        }

        // Исполнение
        manager.deleteAllTask();
        final List<Task> tasks = manager.findAllTasks();

        // Проверка
        assertTrue(tasks.isEmpty(), "Должен быть пустой");
    }

    @Test
    void updateTask() {
        // Подготовка
        Task task = new Task("Test", "Testdescription", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        final int taskId = manager.createTask(task);
        Task taskUdate = new Task("Test TaskUpdate", "Test description Update",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));

        // Исполнение
        manager.updateTask(taskId, taskUdate);
        Task task2 = manager.findTaskById(taskUdate.getId());
        // Проверка
        assertEquals(taskUdate, task2, "Задачи не совпадают");
    }

    @Test
    void deleteTask() {
        // Подготовка
        Task task = new Task("Test addNewTask", "Test addNewTask description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        final int taskId = manager.createTask(task);

        // Исполнение
        manager.deleteTask(taskId);
        Task task1 = manager.findTaskById(taskId);

        // Проверка
        assertNull(task1, "Задача не удалена по ID");
    }

    @Test
    void deleteAllEpic() {
        // Подготовка
        final int counter = 10;
        for (int i = 1; i <= counter; i++) {
            Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
            manager.createEpic(epic);
        }

        // Исполнение
        manager.deleteAllEpic();
        final List<Epic> epics = manager.findAllEpic();

        // Проверка
        assertTrue(epics.isEmpty(), "Должен быть пустой");
    }

    @Test
    void updateEpic() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        Epic epicUpdate = new Epic("Test Update Name", "Test Description Update");

        // Исполнение
        manager.updateEpic(epickId, epicUpdate);
        Epic epic1 = manager.findEpicById(epicUpdate.getId());
        // Проверка
        assertEquals(epicUpdate, epic1);
    }

    @Test
    void IfAtLeastOneSubtaskProgressTheRestOfTheNewOrDoneStatusIsProgress() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "Test Description",
                Status.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("SubTaskName3", "SubTaskDescription3",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("03.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask3);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void allSubtasksAreNewTheStatusOftheEpicisNew() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask 1", "SubTaskDescription",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTaskName2", "SubTaskDescription2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Test SubTaskName3", "SubTaskDescription3", Status.NEW, epickId);
        manager.createSubTask(subTask3);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.NEW, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void ifAllSubTasksDoneStatusEpicDone() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "TestDescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask1", "SubTaskDescription1",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTaskName2", "SubTaskDescription2",
                Status.DONE,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.DONE, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void IfTheSubtasksAreNewAndDoneEpicProgress() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "TestDescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask1", "SubTaskDescription1",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTaskName2", "SubTaskDescription2",
                Status.DONE,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void IfTheSubtasksAreProgressAndEpicProgress() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "TestDescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask1", "SubTaskDescription1",
                Status.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTaskName2", "SubTaskDescription2",
                Status.IN_PROGRESS,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void findAllEpicSubtasks() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        Epic epic1 = manager.findEpicById(epic.getId());
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);

        // Исполнение
        final List<SubTask> subTasks = manager.findAllEpicSubtasks(epic);
        SubTask subTask3 = manager.findSubTaskById(subTask.getId());
        SubTask subTask4 = manager.findSubTaskById(subTask2.getId());

        // Проверка
        assertEquals(epic1.getSubTasksIds().size(), subTasks.size());
        assertEquals(subTask3, subTask,"Подзадача 1 не совпадает");
        assertEquals(subTask4, subTask2,"Подзадача 2 не совпадает");
    }

    @Test
    void deleteEpic() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        manager.findEpicById(epic.getId());
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Test SubTask 3", "SubTask Description 3",
                Status.IN_PROGRESS, epickId);
        manager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Test SubTask 4", "SubTaskDescription 4", Status.NEW, epickId);
        manager.createSubTask(subTask4);

        // Исполнение
        manager.deleteEpic(epickId);
        final Epic epic1 = manager.findEpicById(epickId);
        final List<SubTask> subTasks = manager.findAllEpicSubtasks(epic);

        // Проверка
        assertNull(epic1, "Эпик не удалён");
        assertTrue(subTasks.isEmpty(), "Подзадачи не удалены");
    }

    @Test
    void deleteAllSubTask() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("Test SubTask 3", "SubTask Description 3",
                Status.IN_PROGRESS, epickId);
        manager.createSubTask(subTask3);
        SubTask subTask4 = new SubTask("Test SubTask 4", "SubTaskDescription 4", Status.NEW, epickId);
        manager.createSubTask(subTask4);

        // Исполнение
        manager.deleteAllSubTask();
        final Epic epic1 = manager.findEpicById(epickId);
        final List<SubTask> subTasks = manager.findAllSubTask();

        // Проверка
        assertTrue(subTasks.isEmpty(), "Подзадачи не удалены");
        assertTrue(epic1.getSubTasksIds().isEmpty(), "Подзадачи из Эпика не удалены");
    }

    @Test
    void updateSubTask() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epickId);
        manager.createSubTask(subTask);
        SubTask subTaskUpdate = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.IN_PROGRESS,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epickId);

        // Исполнение
        manager.updateSubTask(subTask.getId(), subTaskUpdate);
        SubTask subTask1 = manager.findSubTaskById(subTaskUpdate.getId());
        // Проверка
        assertEquals(subTask1, subTaskUpdate, "Подзадачи не совпадают");
    }

    @Test
    void deleteSubTaskId() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask);

        // Исполнение
        manager.deleteSubTask(subTask.getId());
        SubTask subTaskTest = manager.findSubTaskById(subTask.getId());

        // Проверка
        assertNull(subTaskTest, "Задача не удалена по ID");
        assertTrue(epic.getSubTasksIds().isEmpty(),"Подзадача не удалена из Эпика");
    }

    @Test
    void findAllTasks() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1",
                Status.NEW, Duration.ofMinutes(30), LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Task task2 = new Task("Task2", "Description Task2",
                Status.IN_PROGRESS, null, LocalDateTime.parse("01.01.2020 - 10:00", dtf));
        manager.createTask(task2);
        Task task3 = new Task("Task3", "Description Task3",
                Status.DONE, Duration.ofMinutes(60), LocalDateTime.parse("02.01.2020 - 09:00", dtf));
        manager.createTask(task3);

        // Исполнение
        final List<Task> tasks = manager.findAllTasks();
        Task task4 = manager.findTaskById(task.getId());
        Task task5 = manager.findTaskById(task2.getId());
        Task task6 = manager.findTaskById(task3.getId());
        final int quantityTask = 3;
        // Проверка
        assertEquals(quantityTask, tasks.size());
        assertEquals(task, task4,"Задача 1 не совпадает");
        assertEquals(task2, task5,"Задача 2 не совпадает");
        assertEquals(task3, task6,"Задача 2 не совпадает");
    }

    @Test
    void deleteAllTask() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1",
                Status.NEW, Duration.ofMinutes(30), LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Task task2 = new Task("Task2", "Description Task2",
                Status.IN_PROGRESS, null, LocalDateTime.parse("01.01.2020 - 10:00", dtf));
        manager.createTask(task2);
        Task task3 = new Task("Task3", "Description Task3",
                Status.DONE, Duration.ofMinutes(60), LocalDateTime.parse("02.01.2020 - 09:00", dtf));
        manager.createTask(task3);

        // Исполнение
        manager.deleteAllTask();
        final List<Task> tasks = manager.findAllTasks();

        // Проверка
        assertTrue(tasks.isEmpty(), "Задачи не удалены");
    }

    @Test
    void findTaskById() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1",
                Status.NEW, Duration.ofMinutes(30), LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);

        // Исполнение
        Task task1 = manager.findTaskById(task.getId());
        final List<Task> taskHistory = manager.getHistory();

        // Проверка
        assertEquals(task1, task, "Подзадачи не совпадают");
        assertEquals(task1, taskHistory.get(0));
    }

    @Test
    void findAllEpic() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("Test SubTask 2", "SubTask Description 2",
                Status.NEW,Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask2);
        Epic epic2 = new Epic("Test Epic 2", "Test Description 2");
        manager.createEpic(epic2);

        // Исполнение
        final List<Epic> epics = manager.findAllEpic();
        final List<SubTask> subTasks = manager.findAllSubTask();
        final int quantity = 2;
        // Проверка

        assertEquals(quantity, epics.size());
        assertTrue(subTasks.contains(subTask), "SubTask 1 не совпадает");
        assertTrue(subTasks.contains(subTask2), "SubTask 2 не совпадает");
        assertEquals(epic.getId(), subTasks.get(0).getEpicId(), "ID Эпика и SubTask.getEpicId не совпадает");
        assertEquals(epic.getId(), subTasks.get(1).getEpicId(), "ID Эпика и SubTask.getEpicId не совпадает");
    }

    @Test
    void findEpicById() {
        // Подготовка
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        manager.findEpicById(epic.getId());

        // Исполнение
        Epic epic1 = manager.findEpicById(epic.getId());

        // Проверка
        assertEquals(epic1, epic, "Подзадачи не совпадают");
    }

    @Test
    void getHistory() {
        // Подготовка
        Task task = new Task("Test Task", "Test Description", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("04.01.2020 - 09:00", dtf));
        manager.createTask(task);
        manager.findTaskById(task.getId());
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask 1", "Test Description", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.parse("03.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask);
        manager.findSubTaskById(subTask.getId());
        SubTask subTask2 = new SubTask("Test SubTask 2", "Test Description 2", Status.DONE,
                Duration.ofMinutes(30), LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask2);
        manager.findSubTaskById(subTask2.getId());
        manager.findEpicById(epic.getId());

        // Исполнение
        final List<Task> tasks = manager.getHistory();
        Optional<Epic> epicHistory = tasks.stream()
                .filter(Objects::nonNull)
                .filter(Epic.class::isInstance)
                .map(Epic.class::cast)
                .findFirst();
        final int quantity = 4;

        // Проверка
        assertEquals(quantity, tasks.size());
        assertTrue(tasks.contains(task),"Задача 1 не совпадает");
        assertEquals(epicHistory.get().getId(), epic.getId(),"Id Эпика не совпадает");
        assertEquals(epicHistory.get().getName(), epic.getName(),"Name Эпика не совпадает");
        assertEquals(epicHistory.get().getDescription(), epic.getDescription(),
                "Description Эпика не совпадает");
        assertEquals(epicHistory.get().getStatus(), subTask.getStatus(),"Status Эпика не совпадает");
        assertTrue(epicHistory.get().getSubTasksIds().contains(subTask.getId()),"Id subTask не совпадает");
        assertTrue(epicHistory.get().getSubTasksIds().contains(subTask2.getId()),"Id subTask2 не совпадает");
        assertEquals(epicHistory.get().getStartTime(), subTask2.getStartTime(),
                "StartTime Эпика и SubTasks не совпадает");
        assertEquals(epicHistory.get().getEndTime(), subTask.getEndTime(),
                "EndTime Эпика и SubTasks не совпадает");
        assertEquals(epicHistory.get().getDuration(), subTask.getDuration().plus(subTask2.getDuration()),
                "Duration Эпика и SubTasks не совпадает");
        assertTrue(tasks.contains(subTask), "subTask не совпадает");
        assertTrue(tasks.contains(subTask2), "subTask2 не совпадает");
    }

    @Test
    void changingTasksThroughSetter() {
        // Подготовка
        Task task = new Task("Test Task", "Test Description", Status.NEW);
        final int id = manager.createTask(task);
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("Test SubTask", "SubTask Description",
                Status.IN_PROGRESS, epic.getId());
        manager.createSubTask(subTask);

        // Исполнение
        task.setId(1000);
        epic.setStatus(Status.DONE);
        subTask.setEpicId(55);
        Task task1 = manager.findTaskById(id);
        Epic epic1 = manager.findEpicById(epic.getId());
        SubTask subTask1 = manager.findSubTaskById(subTask.getId());

        // Проверка
        assertNotEquals(task, task1, "Значения не должны быть равны");
        assertNotEquals(epic, epic1, "Значения не должны быть равны");
        assertNotEquals(subTask, subTask1, "Значения не должны быть равны");
    }
}
