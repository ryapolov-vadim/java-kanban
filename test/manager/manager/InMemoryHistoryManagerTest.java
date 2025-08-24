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

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private SubTask subTask1;
    private SubTask subTask2;
    private DateTimeFormatter dtf;

    @BeforeEach
    void setUp() {
        // Подготовка
        historyManager = new InMemoryHistoryManager();
        dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

        task = new Task("Test Task", "Test Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.parse("04.01.2020 - 09:00", dtf));
        task.setId(1);

        epic = new Epic("Test Epic", "Test Description");
        epic.setId(2);

        subTask1 = new SubTask("Test SubTask 1", "Test Description", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.parse("03.01.2020 - 09:00", dtf), epic.getId());
        subTask1.setId(3);

        subTask2 = new SubTask("Test SubTask 2", "Test Description 2", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.parse("02.01.2020 - 09:00", dtf), epic.getId());
        subTask2.setId(4);
    }

    @Test
    void historyShouldBeEmptyAtStart() {
        // Проверка пустой истории
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void shouldAddTasksToHistoryWithoutDuplicates() {
        // Подготовка
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask1);
        historyManager.add(subTask2);

        //Исполнение
        historyManager.add(subTask1);
        List<Task> history = historyManager.getHistory();

        //Проверка
        assertEquals(4, history.size(), "История должна содержать только уникальные задачи");
        assertEquals(subTask1, history.get(history.size() - 1),
                "Последним элементом должна быть subTask1 после повторного добавления");
    }

    @Test
    void shouldRemoveTaskFromBeginningOfHistory() {
        // Подготовка
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask1);

        //Исполнение
        historyManager.remove(task.getId());
        List<Task> history = historyManager.getHistory();

        //Проверка
        assertEquals(2, history.size(), "После удаления должно остаться 2 задачи");
        assertFalse(history.contains(task), "Первая задача должна быть удалена из истории");
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        // Подготовка
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask1);

        //Исполнение
        historyManager.remove(epic.getId());
        List<Task> history = historyManager.getHistory();

        //Проверка
        assertEquals(2, history.size(), "После удаления должно остаться 2 задачи");
        assertFalse(history.contains(epic), "Эпик должен быть удалён из истории");
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        // Подготовка
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask1);

        //Исполнение
        historyManager.remove(subTask1.getId());
        List<Task> history = historyManager.getHistory();

        //Проверка
        assertEquals(2, history.size(), "После удаления должно остаться 2 задачи");
        assertFalse(history.contains(subTask1), "Последняя задача должна быть удалена");
        assertEquals(epic, history.get(history.size() - 1), "Эпик должен стать последним");
    }

    @Test
    void shouldDoNothingIfRemoveNonExistentTask() {
        // Подготовка
        historyManager.add(task);
        historyManager.add(epic);

        //Исполнение
        historyManager.remove(999); // Несуществующий id
        List<Task> history = historyManager.getHistory();

        //Проверка
        assertEquals(2, history.size(), "История не должна измениться при удалении несуществующей задачи");
    }
}