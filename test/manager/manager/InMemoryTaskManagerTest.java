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
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description", Status.NEW, epicId);
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

        // Проверка
        assertEquals(task, task1,"Задачи не совпадают");
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

        // Проверка
        assertEquals(epicUpdate, epic);
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
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(),"Задачи не совпадают");
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
        assertEquals(Status.NEW, savedEpic.getStatus(),"Задачи не совпадают");
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
        assertEquals(Status.DONE, savedEpic.getStatus(),"Задачи не совпадают");
    }

    @Test
    void findAllEpicSubtasks() {
        // Подготовка
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        manager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2", Status.NEW, epickId);
        manager.createSubTask(subTask2);

        // Исполнение
        final List<SubTask> subTasks = manager.findAllEpicSubtasks(epic);

        // Проверка
        assertEquals(epic.getSubTasksIds().size(), subTasks.size());
        assertTrue(subTasks.contains(subTask), "Подзадача 1 не совпадает");
        assertTrue(subTasks.contains(subTask2),"Подзадача 2 не совпадает");
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
        assertTrue(epic.getSubTasksIds().isEmpty(), "Подзадачи из Эпика не удалены");
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

        // Проверка
        assertEquals(subTask2, subTask, "Подзадачи не совпадают");
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
        assertTrue(epic.getSubTasksIds().isEmpty(), "Подзадача не удалена из Эпика");
    }
}