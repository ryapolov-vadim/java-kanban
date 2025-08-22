package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.exception.ManagerFileInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    void setupFile() {
        try {
            file = File.createTempFile("backupTest", ".CSV");
            manager = getTaskManager();
        } catch (IOException e) {
            throw new ManagerFileInitializationException("Ошибка создания файла для теста: " + e.getMessage());
        }
    }

    @Override
    FileBackedTaskManager getTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    void savingMultipleTasksToAFile() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Epic epic = new Epic("Epic1", "Description Epic1");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask1", "Description SubTask1",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask);

        // Исполнение
        Epic epic2 = manager.findEpicById(epic.getId());
        try {
            List<String> taskList;
            try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                taskList = stream
                        .filter(s ->
                                (!s.equals("id,type,name,status,description,epic,duration,startTime,endTime")))
                        .filter(Objects::nonNull)
                        .toList();
            }
            String[] taskString = taskList.get(0).split(",");
            String[] epcickString = taskList.get(1).split(",");
            String[] subTaskString = taskList.get(2).split(",");

            // Проверка
            assertEquals(task.getName(), taskString[2], "Имя не совпадает");
            assertEquals(task.getDescription(), taskString[4], "Описание не совпадает");
            assertEquals(task.getStatus(), Status.valueOf(taskString[3]), "Статус не совпадает");
            assertEquals(task.getId(), Integer.parseInt(taskString[0]), "ID не совпадает");
            assertEquals(task.getDuration(), Duration.ofMinutes(Integer.parseInt(taskString[6])),
                    "Duration не совпадает");
            assertEquals(task.getStartTime(), LocalDateTime.parse(taskString[7], dtf), "StartTime не совпадает");
            assertEquals(task.getEndTime(), LocalDateTime.parse(taskString[8], dtf), "EndTime не совпадает");

            assertEquals(epic.getName(), epcickString[2], "Имя не совпадает");
            assertEquals(epic.getDescription(), epcickString[4], "Описание не совпадает");
            assertEquals(subTask.getStatus(), Status.valueOf(epcickString[3]), "Статус не совпадает");
            assertEquals(epic2.getId(), Integer.parseInt(epcickString[0]), "ID не совпадает");
            assertEquals(epic2.getDuration(), Duration.ofMinutes(Integer.parseInt(subTaskString[6])),
                    "Duration не совпадает");
            assertEquals(epic2.getStartTime(), LocalDateTime.parse(subTaskString[7], dtf),
                    "StartTime не совпадает");
            assertEquals(epic2.getEndTime(), LocalDateTime.parse(subTaskString[8], dtf), "EndTime не совпадает");
            assertEquals(epic2.getSubTasksIds().get(0), Integer.parseInt(subTaskString[0]),
                    "ID подзадачи не совпадает");

            assertEquals(subTask.getName(), subTaskString[2], "Имя не совпадает");
            assertEquals(subTask.getDescription(), subTaskString[4], "Описание не совпадает");
            assertEquals(subTask.getStatus(), Status.valueOf(subTaskString[3]), "Статус не совпадает");
            assertEquals(subTask.getId(), Integer.parseInt(subTaskString[0]), "ID не совпадает");
            assertEquals(subTask.getDuration(), Duration.ofMinutes(Integer.parseInt(subTaskString[6])),
                    "Duration не совпадает");
            assertEquals(subTask.getStartTime(), LocalDateTime.parse(subTaskString[7], dtf),
                    "StartTime не совпадает");
            assertEquals(subTask.getEndTime(), LocalDateTime.parse(subTaskString[8], dtf), "EndTime не совпадает");
            assertEquals(subTask.getEpicId(), Integer.parseInt(subTaskString[5]),
                    "ID Эпика не совпадает");
        } catch (IOException e) {
            throw new ManagerFileInitializationException("Ошибка при проверке сохранённого файла: " + e.getMessage());
        }
    }

    @Test
    void downloadingDataFromAFile() {
        // Подготовка
        Task task = new Task("Task1", "Description Task1", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Epic epic = new Epic("Epic1", "Description Epic1");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask1", "Description SubTask1",
                Status.DONE, Duration.ofMinutes(30),
                LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());
        manager.createSubTask(subTask);

        // Исполнение
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Task task1 = fileBackedTaskManager.findTaskById(task.getId());
        Epic epic1 = fileBackedTaskManager.findEpicById(epic.getId());
        SubTask subTask1 = fileBackedTaskManager.findSubTaskById(subTask.getId());


        // Проверка
        assertEquals(task, task1, "Задачи не совпадают");
        assertEquals(epic.getName(), epic1.getName(), "Имя Эпика не совпадает");
        assertEquals(epic.getDescription(), epic1.getDescription(), "Описание Эпика не совпадает");
        assertEquals(epic.getId(), epic1.getId(), "ID Эпика не совпадает");
        assertEquals(subTask.getStartTime(), epic1.getStartTime(), "StartTime Эпика не совпадает");
        assertEquals(subTask.getDuration(), epic1.getDuration(), "Duration Эпика не совпадает");
        assertEquals(subTask.getEndTime(), epic1.getEndTime(), "EndTime Эпика не совпадает");
        assertEquals(subTask.getId(), epic1.getSubTasksIds().get(0), "ID Подзадачи у Эпика не совпадает");
        assertEquals(subTask.getStatus(), epic1.getStatus(), "Статус Эпика и Подзадачи не совпадает");
        assertEquals(subTask, subTask1, "Подзадачи не совпадают");
    }

    @Test
    void savingAndUploadingAnEmptyFile() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(fileBackedTaskManager.findAllTasks().isEmpty(), "Задачи должны быть пустыми");
        assertTrue(fileBackedTaskManager.findAllEpic().isEmpty(), "Эпики должны быть пустыми");
        assertTrue(fileBackedTaskManager.findAllSubTask().isEmpty(), "Сабтаски должны быть пустыми");
    }

    @Test
    void loadFromValidFileShouldNotThrowException() {
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(file));
    }

    @Test
    void constructorWithValidFileShouldNotThrowException() {
        assertDoesNotThrow(() -> new FileBackedTaskManager(file),
                "Загрузка из допустимого файла не должна вызывать исключение");
    }

    @Test
    void saveAndLoadWithValidDataShouldNotThrowException() {
        Task task = new Task("Task1", "Description Task1", Status.NEW, Duration.ofMinutes(30),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        assertDoesNotThrow(() -> {
            manager.createTask(task);
            FileBackedTaskManager.loadFromFile(file);
        }, "Сохранение и загрузка с использованием корректных данных не должны вызывать исключение");
    }

    @Test
    void loadFromFileWithNullFileShouldThrowException() {
        assertThrows(ManagerFileInitializationException.class, () -> FileBackedTaskManager.loadFromFile(null),
                "Загрузка менеджера с переданным Null вместо файла, должна вызывать исключение");
    }

    @Test
    void loadFromFileWithNonExistentFileShouldThrowException() {
        File nonExistentFile = new File("non_existent_file.csv");
        assertThrows(ManagerFileInitializationException.class, () ->
                        FileBackedTaskManager.loadFromFile(nonExistentFile),
                "Загрузка из файла, которого не существует, должна вызывать исключение");
    }
}
