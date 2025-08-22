package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
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

        //Исполнение
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 30));
        manager.createTask(task2);

        List<Task> tasks = manager.findAllTasks();

        //Проверка
        assertEquals(1, tasks.size());
        assertEquals("Task1", tasks.get(0).getName());
    }

    @Test
    void OverlappingTasksShouldNotBePrioritized() {
        //Подготовка
        Task task1 = new Task("Task1", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 0));
        manager.createTask(task1);

        //Исполнение
        Task task2 = new Task("Task2", "Desc", Status.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 21, 10, 30));
        manager.createTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();

        //Проверка
        assertEquals(1, prioritized.size(), "Пересекающаяся задача попала в множество");
        assertEquals(task1, prioritized.get(0), "Задача не добавилась в приоритетный список");
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
        manager.updateTask(id2, updated);
        List<Task> taskList = manager.getPrioritizedTasks();

        //Проверка
        assertNotEquals(updated, task2, "Задача при обновлении с невалидным временем попала в менеджер");
        assertEquals(task2, taskList.get(1), "Обновление с невалидными временем обновило Задачу в множестве");
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

        //Исполнение
        manager.createSubTask(sub2);
        List<SubTask> allSubs = manager.findAllSubTask();

        //Проверка
        assertEquals(1, allSubs.size(), "Пересекающийся сабтаск был добавлен.");
        assertEquals("Sub1", allSubs.get(0).getName());
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
    void shouldKeepOldSubTaskWhenUpdateCausesOverlap() {
        //Подготовка
        Epic epic = new Epic("Epic", "Desc");
        int epicId = manager.createEpic(epic);
        SubTask sub1 = new SubTask("Sub1", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 09:00", dtf), epicId);
        int id1 = manager.createSubTask(sub1);
        SubTask sub2 = new SubTask("Sub2", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 11:00", dtf), epicId);
        int id2 = manager.createSubTask(sub2);

        //Исполнение
        SubTask updated = new SubTask("UpdatedSub2", "Desc", Status.NEW,
                Duration.ofMinutes(60),
                LocalDateTime.parse("01.01.2020 - 09:30", dtf), epicId);
        manager.updateSubTask(id2, updated);
        SubTask fromManager = manager.findSubTaskById(id2);

        //Проверка
        assertEquals(LocalDateTime.parse("01.01.2020 - 11:00", dtf), fromManager.getStartTime(),
                "При пересечении старая версия сабтаска должна сохраниться.");
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

        //Исполнение
        manager.createSubTask(sub);
        List<SubTask> allSubs = manager.findAllSubTask();

        //Проверка
        assertTrue(allSubs.isEmpty(), "Пересекающийся сабтаск не должен добавляться.");
    }
}