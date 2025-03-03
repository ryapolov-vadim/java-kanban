package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager manager;

    private static Task task, task3Update;
    private static Epic epic;
    private static SubTask subTask;
    private static int idTask, idEpic, idSubTask, limit, limitHistiry;

    @BeforeEach
    void beforeAll() {
        // Подготовка
        manager = Managers.getDefault();
        task = new Task("TaskTestName", "TaskTestDiscription", Status.NEW);
        task3Update = new Task("TaskTestName3Update", "TaskTestDiscription3Update", Status.IN_PROGRESS);
        idTask = manager.createTask(task);

        epic = new Epic("EpicTestName", "EpicTestDiscription");
        idEpic = manager.createEpic(epic);
        subTask = new SubTask("subTaskName", "subTaskDiscription", Status.NEW, idEpic);
        idSubTask = manager.createSubTask(subTask);
    }

    @Test
    void saveThepreviousVersionOfTheTaskAndItsData() {
        //Подготовка
        Task task1 = manager.findTaskById(idTask);
        List<Task> history = manager.getHistory();
        assertEquals(task1, history.get(task1.getId()));
        Task taskOldVersion = new Task(task1.getName(), task1.getDescription(), task1.getStatus());
        taskOldVersion.setId(task1.getId());
        //Исполнение
        manager.updateTask(idTask, task3Update);
        manager.findTaskById(idTask);
        List<Task> history2 = manager.getHistory();
        //Проверка
        assertEquals(2, history2.size());
        assertEquals(taskOldVersion, history2.get(0));
        assertEquals(task1, history2.get(1));
    }

    @Test
    void mustStoreNoMoreThan10Tasks() {
        //Подготовка
        limit = 4;
        limitHistiry = 10;
        //Исполнение
        for (int i = 0; i < limit; i++) {
            manager.findTaskById(idTask);
        }
        for (int i = 0; i < limit; i++) {
            manager.findEpicById(idEpic);
        }
        for (int i = 0; i < limit; i++) {
            manager.findSubTaskById(idSubTask);
        }
        //Проверка
        List<Task> history = manager.getHistory();
        assertEquals(limitHistiry, history.size());
    }

    @Test
    void canOnlyAddAnExistingTask() {
        //Подготовка
        manager.findTaskById(-100);
        manager.findEpicById(-100);
        manager.findSubTaskById(-100);
        //Исполнение
        List<Task> history = manager.getHistory();
        //Проверка
        assertEquals(0, history.size());
    }

}