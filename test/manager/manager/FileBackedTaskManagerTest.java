package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.exception.ManagerFileInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    FileBackedTaskManager fBTaskManager;
    File tempFile;

    @BeforeEach
    void setupClass() {
        try {
            tempFile = File.createTempFile("backupTest", ".CSV");
            fBTaskManager = new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            String errorMessage = "Ошибка создания менеджера";
            throw new ManagerFileInitializationException(errorMessage + e.getMessage());
        }
    }

    @Test
    void deleteAllTask() {
        //Подготовка
        Task task = new Task("Task1", "Description Task1", Status.NEW);
        fBTaskManager.createTask(task);
        Task task2 = new Task("Task2", "Description Task2", Status.IN_PROGRESS);
        fBTaskManager.createTask(task2);
        Task task3 = new Task("Task3", "Description Task3", Status.DONE);
        fBTaskManager.createTask(task3);

        // Исполнение
        fBTaskManager.deleteAllTask();
        final List<Task> tasks = fBTaskManager.findAllTasks();

        // Проверка
        assertTrue(tasks.isEmpty(), "Должен быть пустой");
    }

    @Test
    void createTask() {
        Task task = new Task("Task1", "Description Task1", Status.NEW);
        final int taskId = fBTaskManager.createTask(task);

        // Исполнение
        final Task savedTask = fBTaskManager.findTaskById(taskId);

        // Проверка
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        // Исполнение
        final List<Task> tasks = fBTaskManager.findAllTasks();

        // Проверка
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        // Подготовка
        Task task = new Task("Test", "Testdescription", Status.NEW);
        final int taskId = fBTaskManager.createTask(task);
        Task task1 = new Task("Test NewTask", "Test NewTask description", Status.DONE);

        // Исполнение
        fBTaskManager.updateTask(taskId, task1);
        Task task2 = fBTaskManager.findTaskById(task1.getId());
        // Проверка
        assertEquals(task1, task2, "Задачи не совпадают");
    }

    @Test
    void deleteTask() {
        // Подготовка
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        final int taskId = fBTaskManager.createTask(task);

        // Исполнение
        fBTaskManager.deleteTask(taskId);
        Task task1 = fBTaskManager.findTaskById(taskId);

        // Проверка
        assertNull(task1, "Задача не удалена по ID");
    }

    @Test
    void deleteAllEpic() {
        //Подготовка
        Epic epic = new Epic("Epic1", "Description Epic1");
        Epic epic1 = new Epic("Epic2", "Description Epic2");
        fBTaskManager.createEpic(epic);
        fBTaskManager.createEpic(epic1);

        // Исполнение
        fBTaskManager.deleteAllEpic();
        final List<Epic> tasks = fBTaskManager.findAllEpic();

        // Проверка
        assertTrue(tasks.isEmpty(), "Должен быть пустой");
    }

    @Test
    void createEpic() {
        // Подготовка
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = fBTaskManager.createEpic(epic);

        // Исполнение
        final Epic savedEpic = fBTaskManager.findEpicById(epicId);

        // Проверка
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        // Исполнение
        final List<Epic> epics = fBTaskManager.findAllEpic();

        // Проверка
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpic() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = fBTaskManager.createEpic(epic);
        Epic epicUpdate = new Epic("TestNameUpdate", "TestdescriptionUpdate");

        // Исполнение
        fBTaskManager.updateEpic(epickId, epicUpdate);
        Epic epic1 = fBTaskManager.findEpicById(epicUpdate.getId());

        // Проверка
        assertEquals(epicUpdate, epic1);
    }

    @Test
    void deleteEpic() {
        // Подготовка
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = fBTaskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        fBTaskManager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("SubTaskName2", "SubTaskDescription2", Status.NEW, epickId);
        fBTaskManager.createSubTask(subTask2);

        // Исполнение
        fBTaskManager.deleteEpic(epickId);
        final Epic epic1 = fBTaskManager.findEpicById(epickId);
        final List<SubTask> subTasks = fBTaskManager.findAllEpicSubtasks(epic);

        // Проверка
        assertNull(epic1, "Эпик не удалён");
        assertTrue(subTasks.isEmpty(), "Подзадачи не удалены");
    }

    @Test
    void deleteAllSubTask() {
        //Подготовка
        Epic epic = new Epic("Epic1", "Description Epic1");
        fBTaskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask1", "Description SubTask1", Status.DONE, epic.getId());
        fBTaskManager.createSubTask(subTask);

        // Исполнение
        fBTaskManager.deleteAllSubTask();
        final List<SubTask> tasks = fBTaskManager.findAllSubTask();

        // Проверка
        assertTrue(tasks.isEmpty(), "Должен быть пустой");
    }

    @Test
    void createSubTask() {
        // Подготовка
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = fBTaskManager.createEpic(epic);

        // Подготовка
        SubTask subTask = new SubTask("Test addNewSubTask", "Test addNewSubTask description",
                Status.NEW, epicId);
        final int subTaskId = fBTaskManager.createSubTask(subTask);

        // Исполнение
        final SubTask subTasks = fBTaskManager.findSubTaskById(subTaskId);

        // Проверка
        assertNotNull(subTasks, "Задача не найдена.");
        assertEquals(subTasks, subTask, "Задачи не совпадают.");

        // Исполнение
        final List<SubTask> subTasks1 = fBTaskManager.findAllSubTask();

        // Проверка
        assertNotNull(subTasks1, "Задачи не возвращаются.");
        assertEquals(1, subTasks1.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks1.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateSubTask() {
        // Подготовка
        Epic epic = new Epic("Test", "Testdescription");
        final int epickId = fBTaskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        final int subTaskId = fBTaskManager.createSubTask(subTask);
        SubTask subTask2 = new SubTask("subTaskUpdate2", "SubTaskUpdateDescription2",
                Status.IN_PROGRESS, epickId);

        // Исполнение
        fBTaskManager.updateSubTask(subTaskId, subTask2);
        SubTask subTask1 = fBTaskManager.findSubTaskById(subTask2.getId());
        // Проверка
        assertEquals(subTask1, subTask2, "Подзадачи не совпадают");
    }

    @Test
    void deleteSubTask() {
        // Подготовка
        Epic epic = new Epic("TestName", "Testdescription");
        final int epickId = fBTaskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTaskName", "SubTaskDescription", Status.NEW, epickId);
        final int subTaskId = fBTaskManager.createSubTask(subTask);

        // Исполнение
        fBTaskManager.deleteSubTask(subTaskId);
        SubTask subTask1 = fBTaskManager.findSubTaskById(subTaskId);

        // Проверка
        assertNull(subTask1, "Задача не удалена по ID");
        assertNotEquals(epic.getSubTasksIds().size(), subTaskId, "Подзадача не удалена из Эпика");
    }

    @Test
    void savingMultipleTasks() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1", Status.NEW);
        int idTask = fBTaskManager.createTask(task);

        Epic epic = new Epic("Epic1", "Description Epic1");
        int idEpic = fBTaskManager.createEpic(epic);
        Epic epic2 = new Epic("Epic2", "Description Epic2");
        int idEpic2 = fBTaskManager.createEpic(epic2);

        SubTask subTask = new SubTask("SubTask1", "Description SubTask1", Status.DONE, epic.getId());
        int idSubtask = fBTaskManager.createSubTask(subTask);

        // Исполнение
        Map<String, String[]> backupFile = new HashMap<>();
        //Сохраняю ключ-значение, где ключ - это 0 индекс, значение - вся строка разбитая на массив, через запятую
        try (BufferedReader br = new BufferedReader(new FileReader(tempFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tack = line.split(",");
                backupFile.put(tack[0], tack);
            }
        } catch (IOException e) {
            String errorMessage = "Ошибка при загрузке менеджера";
            throw new ManagerFileInitializationException(errorMessage + e.getMessage());
        }

        // Проверка
        for (Map.Entry<String, String[]> entry : backupFile.entrySet()) {
            if (entry.getKey().equals("id")) {
                assertArrayEquals(new String[]{"id", "type", "name", "status", "description", "epic"}, entry.getValue(),
                        "Заголовок не совпадает");

            } else if (entry.getKey().equals("1")) {
                assertEquals(String.valueOf(idTask), entry.getValue()[0], "Id не совпадает");
                assertEquals(TaskType.TASK.toString(), entry.getValue()[1], "Type не совпадает");
                assertEquals(task.getName(), entry.getValue()[2], "Имя не совпадает");
                assertEquals(task.getStatus().toString(), entry.getValue()[3], "Status не совпадает");
                assertEquals(task.getDescription(), entry.getValue()[4], "Description не совпадает");
            } else if (entry.getKey().equals("2")) {
                assertEquals(String.valueOf(idEpic), entry.getValue()[0], "Id не совпадает");
                assertEquals(TaskType.EPIC.toString(), entry.getValue()[1], "Type не совпадает");
                assertEquals(epic.getName(), entry.getValue()[2], "Имя не совпадает");
                assertEquals(subTask.getStatus().toString(), entry.getValue()[3], "Status не совпадает");
                assertEquals(epic.getDescription(), entry.getValue()[4], "Description не совпадает");
            } else if (entry.getKey().equals("3")) {
                assertEquals(String.valueOf(idEpic2), entry.getValue()[0], "Id не совпадает");
                assertEquals(TaskType.EPIC.toString(), entry.getValue()[1], "Type не совпадает");
                assertEquals(epic2.getName(), entry.getValue()[2], "Имя не совпадает");
                assertEquals(epic2.getStatus().toString(), entry.getValue()[3], "Status не совпадает");
                assertEquals(epic2.getDescription(), entry.getValue()[4], "Description не совпадает");
            } else {
                assertEquals(String.valueOf(idSubtask), entry.getValue()[0], "Id не совпадает");
                assertEquals(TaskType.SUBTASK.toString(), entry.getValue()[1], "Type не совпадает");
                assertEquals(subTask.getName(), entry.getValue()[2], "Имя не совпадает");
                assertEquals(subTask.getStatus().toString(), entry.getValue()[3], "Status не совпадает");
                assertEquals(subTask.getDescription(), entry.getValue()[4], "Description не совпадает");
                assertEquals(epic.getId(), subTask.getEpicId(), "Epic не совпадает");
            }
        }
    }

    @Test
    void downloadingDataFromAFile() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1", Status.NEW);
        fBTaskManager.createTask(task);

        Epic epic = new Epic("Epic1", "Description Epic1");
        fBTaskManager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask1", "Description SubTask1", Status.DONE, epic.getId());
        fBTaskManager.createSubTask(subTask);

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);


        // Исполнение
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = fileBackedTaskManager.findAllTasks();
        List<Epic> epics = fileBackedTaskManager.findAllEpic();
        List<SubTask> subTasks = fileBackedTaskManager.findAllSubTask();

        // Проверка
        for (Task task1 : tasks) {
            assertEquals(task, task1, "Задачи не совпадает");
        }

        for (Epic epic1 : epics) {
            epic.setStatus(subTask.getStatus()); // Статус начального эпика изменён SubTask'ом
            assertEquals(epic, epic1, "Задачи не совпадает");
        }

        for (SubTask subTask1 : subTasks) {
            assertEquals(subTask, subTask1, "Задачи не совпадает");
        }
    }

    @Test
    void savingAndUploadingAnEmptyFile() {
        // Подготовка
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        // Исполнение
        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = fileBackedTaskManager.findAllTasks();
        List<Epic> epics = fileBackedTaskManager.findAllEpic();
        List<SubTask> subTasks = fileBackedTaskManager.findAllSubTask();

        // Проверка
        for (Task task1 : tasks) {
            assertTrue(tasks.isEmpty(), "Задачи должны быть пустыми");
        }

        for (Epic epic1 : epics) {
            assertTrue(epics.isEmpty(), "Эпики должны быть пустыми");
        }

        for (SubTask subTask1 : subTasks) {
            assertTrue(subTasks.isEmpty(), "Сабтаски должны быть пустыми");
        }
    }
}