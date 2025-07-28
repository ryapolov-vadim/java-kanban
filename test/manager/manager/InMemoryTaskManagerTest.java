package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    TaskManager manager;

    @BeforeEach
    void setupClass() {
        manager = Managers.getDefault();
    }

    @Test
    void taskManagerInit() {
        assertNotNull(manager, "ошибка создания менеджера");
    }

    @Test
    void addNewTask() {
        // Подготовка
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
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
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
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
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = manager.createEpic(epic);

        // Подготовка
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                Status.NEW, epicId);
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
        final int counter = 10;
        for (int i = 1; i <= counter; i++) {
            Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
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
        Task task = new Task("Test", "Testdescription", Status.NEW);
        final int taskId = manager.createTask(task);
        Task task1 = new Task("Test NewTask", "Test NewTask description", Status.DONE);

        // Исполнение
        manager.updateTask(taskId, task1);
        Task task2 = manager.findTaskById(task1.getId());
        // Проверка
        assertEquals(task1, task2, "Задачи не совпадают");
    }

    @Test
    void deleteTask() {
        // Подготовка
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
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
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = manager.createEpic(epic);
        Epic epicUpdate = new Epic("TestNameUpdate", "TestdescriptionUpdate");

        // Исполнение
        manager.updateEpic(epickId, epicUpdate);
        Epic epic1 = manager.findEpicById(epicUpdate.getId());
        // Проверка
        assertEquals(epicUpdate, epic1);
    }

    @Test
    void IfAtLeastOneSubtaskProgressTheRestOfTheNewOrDoneStatusIsProgress() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.IN_PROGRESS, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2",
                Status.NEW, epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("SubTaskName3", "SubTaskDescription3", Status.DONE, epickId);
        manager.createSubTask(subTask3);

        // Исполнение
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void ifOneNewSubtaskAndAllOtherDoneAreEpicNewStatus() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2",
                Status.DONE, epickId);
        manager.createSubTask(subTask2);
        SubTask subTask3 = new SubTask("SubTaskName3", "SubTaskDescription3", Status.DONE, epickId);
        manager.createSubTask(subTask3);
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.NEW, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void ifAllSubTasksDoneStatusEpicDone() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.DONE, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2",
                Status.DONE, epickId);
        manager.createSubTask(subTask2);
        final Epic savedEpic = manager.findEpicById(epickId);

        // Проверка
        assertEquals(Status.DONE, savedEpic.getStatus(), "Задачи не совпадают");
    }

    @Test
    void findAllEpicSubtasks() {
        // Подготовка
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        Epic epic1 = manager.findEpicById(epic.getId());
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2", Status.NEW, epickId);
        manager.createSubTask(subTask2);

        // Исполнение
        final List<SubTask> subTasks = manager.findAllEpicSubtasks(epic);
        SubTask subTask3 = manager.findSubTaskById(subTask.getId());
        SubTask subTask4 = manager.findSubTaskById(subTask2.getId());

        // Проверка
        assertEquals(epic1.getSubTasksIds().size(), subTasks.size());
        assertTrue(subTasks.contains(subTask3), "Подзадача 1 не совпадает");
        assertTrue(subTasks.contains(subTask4), "Подзадача 2 не совпадает");
    }

    @Test
    void deleteEpic() {
        // Подготовка
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2", Status.NEW, epickId);
        manager.createSubTask(subTask2);

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
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDiscription2", Status.NEW, epickId);
        manager.createSubTask(subTask2);

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
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        final int subTaskId = manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("subTaskUpdate2", "SubTaskUpdateDescription2",
                Status.IN_PROGRESS, epickId);

        // Исполнение
        manager.updateSubTask(subTaskId, subTask2);
        SubTask subTask1 = manager.findSubTaskById(subTask2.getId());
        // Проверка
        assertEquals(subTask1, subTask2, "Подзадачи не совпадают");
    }

    @Test
    void deleteSubTask() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        final int subTaskId = manager.createSubTask(subTask);

        // Исполнение
        manager.deleteSubTask(subTaskId);
        SubTask subTask1 = manager.findSubTaskById(subTaskId);

        // Проверка
        assertNull(subTask1, "Задача не удалена по ID");
        assertNotEquals(epic.getSubTasksIds().size(), subTaskId, "Подзадача не удалена из Эпика");
    }

    @Test
    void findAllTasks() {
        // Подготовка
        Task task = new Task("Test", "Testdescription", Status.NEW);
        manager.createTask(task);
        Task task2 = new Task("TestTaskName", "TaskDescription", Status.DONE);
        manager.createTask(task2);
        Task task3 = new Task("TestTaskName2", "TaskDescription2", Status.IN_PROGRESS);
        manager.createTask(task3);

        // Исполнение
        final List<Task> tasks = manager.findAllTasks();
        Task task4 = manager.findTaskById(task.getId());
        Task task5 = manager.findTaskById(task2.getId());
        Task task6 = manager.findTaskById(task3.getId());
        final int quantityTask = tasks.size();
        // Проверка
        assertEquals(quantityTask, tasks.size());
        assertTrue(tasks.contains(task4), "Задача 1 не совпадает");
        assertTrue(tasks.contains(task5), "Задача 2 не совпадает");
        assertTrue(tasks.contains(task6), "Задача 2 не совпадает");
    }

    @Test
    void deleteAllTask() {
        // Подготовка
        Task task = new Task("Test", "Testdescription", Status.NEW);
        manager.createTask(task);
        Task task2 = new Task("TestTaskName", "TaskDescription", Status.DONE);
        manager.createTask(task2);
        Task task3 = new Task("TestTaskName2", "TaskDescription2", Status.IN_PROGRESS);
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
        Task task = new Task("Test", "Testdescription", Status.NEW);
        final int id = manager.createTask(task);

        // Исполнение
        Task task1 = manager.findTaskById(id);

        // Проверка
        assertEquals(task1, task, "Подзадачи не совпадают");
    }

    @Test
    void findAllEpic() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        manager.createEpic(epic);
        Epic epicTwo = new Epic("TestNameEpicTwo", "TestdescriptionEpicTwo");
        manager.createEpic(epicTwo);

        // Исполнение
        final List<Epic> epics = manager.findAllEpic();
        final int quantity = epics.size();
        // Проверка

        assertEquals(quantity, epics.size());
        assertTrue(epics.contains(epic), "Эпик 1 не совпадает");
        assertTrue(epics.contains(epicTwo), "Эпик 2 не совпадает");
    }

    @Test
    void findEpicById() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        manager.createEpic(epic);
        Epic epicTwo = new Epic("TestNameEpicTwo", "TestdescriptionEpicTwo");
        final int id = manager.createEpic(epicTwo);

        // Исполнение
        Epic epic1 = manager.findEpicById(id);

        // Проверка
        assertEquals(epic1, epicTwo, "Подзадачи не совпадают");
    }

    @Test
    void getHistory() {
        // Подготовка
        Task task = new Task("Test", "Testdescription", Status.NEW);
        final int id = manager.createTask(task);
        manager.findTaskById(id);
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        manager.findEpicById(epickId);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        final int sunTaskId = manager.createSubTask(subTask);
        manager.findSubTaskById(sunTaskId);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2", Status.NEW, epickId);
        final int sibTaskId2 = manager.createSubTask(subTask2);
        manager.findSubTaskById(sibTaskId2);

        // Исполнение
        final List<Task> tasks = manager.getHistory();
        final int quantity = tasks.size();
        Task task1 = manager.findTaskById(id);
        Epic epic1 = manager.findEpicById(epickId);
        SubTask subTask1 = manager.findSubTaskById(sunTaskId);
        SubTask subTask3 = manager.findSubTaskById(sibTaskId2);

        // Проверка
        assertEquals(quantity, tasks.size());
        assertTrue(tasks.contains(task1), "Задача 1 не совпадает");
        assertTrue(tasks.contains(epic1), "Задача 2 не совпадает");
        assertTrue(tasks.contains(subTask1), "Задача 2 не совпадает");
        assertTrue(tasks.contains(subTask3), "Задача 2 не совпадает");
    }

    @Test
    void changingTasksThroughSetter() {
        // Подготовка
        Task task = new Task("Test", "Testdescription", Status.NEW);
        final int id = manager.createTask(task);
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        final int sunTaskId = manager.createSubTask(subTask);

        // Исполнение
        task.setId(1000);
        epic.setStatus(Status.DONE);
        subTask.setEpicId(55);
        Task task1 = manager.findTaskById(id);
        Epic epic1 = manager.findEpicById(epickId);
        SubTask subTask1 = manager.findSubTaskById(sunTaskId);

        // Проверка
        assertNotEquals(task, task1, "Значения не должны быть равны");
        assertNotEquals(epic, epic1, "Значения не должны быть равны");
        assertNotEquals(subTask, subTask1, "Значения не должны быть равны");
    }
}