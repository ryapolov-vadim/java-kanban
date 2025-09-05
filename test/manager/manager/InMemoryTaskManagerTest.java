package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.exception.TimeVerificationException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldNotAddOverlappingTask() {
        // Подготовка
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 0));
        manager.createTask(task1);
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 30));

        //Проверка
        assertThrows(TimeVerificationException.class, () -> manager.createTask(task2),
                "Пересекающаяся задача должна вызывать исключение");
    }

    @Test
    void shouldKeepOldTaskWhenUpdateCausesOverlap() {
        //Подготовка
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 0));
        manager.createTask(task1);
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 12, 0));
        int id2 = manager.createTask(task2);

        //Исполнение
        Task updated = new Task("Updated", "Desc", Status.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 30));

        //Проверка
        assertThrows(TimeVerificationException.class, () -> manager.updateTask(id2, updated),
                "Обновляемая задача при пересечении времени, должна вызывать исключение");
    }

    @Test
    void shouldNotAddOverlappingSubTask() {
        //Подготовка
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        SubTask sub1 = new SubTask("Sub1", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 10:00", dtf), epicId);
        manager.createSubTask(sub1);
        SubTask sub2 = new SubTask("Sub2", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 10:30", dtf), epicId);

        //Проверка
        assertThrows(TimeVerificationException.class, () -> manager.createSubTask(sub2),
                "Пересекающаяся подзадача должна вызывать исключение");
    }

    @Test
    void shouldAddNonOverlappingSubTasks() {
        //Подготовка
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        SubTask sub1 = new SubTask("Sub1", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epicId);
        manager.createSubTask(sub1);

        //Исполнение
        SubTask sub2 = new SubTask("Sub2", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 10:00", dtf), epicId);
        manager.createSubTask(sub2); // стык по времени — допустимо
        List<SubTask> allSubs = manager.findAllSubTask();

        //Проверка
        assertEquals(2, allSubs.size(), "Оба сабтаска должны быть добавлены.");
    }

    @Test
    void subTaskShouldNotOverlapWithTask() {
        //Подготовка
        Task task = new Task("Task", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf));
        manager.createTask(task);
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        SubTask sub = new SubTask("Sub", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 09:30", dtf), epicId);

        //Проверка
        assertThrows(TimeVerificationException.class, () -> manager.createSubTask(sub),
                "Пересекающаяся подзадача с задачей должна вызывать исключение");
    }
}