package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static TaskManager manager;

    private static Task task, task3Update;
    private static Epic epic;
    private static SubTask subTask, subTask2;
    private static int idTask, idEpic, idSubTask, idSubTask2, limit, limitHistiry;

    @BeforeEach
    void beforeAll() {
        // Подготовка
        manager = Managers.getDefault();
        task = new Task("TaskTestName", "TaskTestDiscription", Status.NEW);
        idTask = manager.createTask(task);
        task3Update = new Task("TaskTestName3Update", "TaskTestDiscription3Update", Status.IN_PROGRESS);


        epic = new Epic("EpicTestName", "EpicTestDiscription");
        idEpic = manager.createEpic(epic);
        subTask = new SubTask("subTaskName1", "subTaskDiscription1", Status.NEW, idEpic);
        idSubTask = manager.createSubTask(subTask);
        subTask2 = new SubTask("subTaskName2", "subTaskDiscription2", Status.NEW, idEpic);
        idSubTask2 = manager.createSubTask(subTask2);
    }

    @Test
    void SavingATaskToTheEndOfATwoLinkedListAndDeletingTheSameTaskViewedEarlier() {
        //Подготовка
        manager.findTaskById(idTask);
        manager.findEpicById(idEpic);
        manager.findSubTaskById(idSubTask);
        manager.findSubTaskById(idSubTask2);
        manager.findSubTaskById(idSubTask); //повторный просмотр

        //Исполнение
        final int quantity = 4;
        final List<Task> listHistory = manager.getHistory();
        final int value = listHistory.size();

        //Проверка
        assertEquals(quantity, listHistory.size());
        assertEquals(listHistory.get(value - 1), subTask, "Подзадачи не совпадают");
    }

    @Test
    void DeletingFromTheBrowsingHistoryIfASubtaskIsDeleted() {
        //Подготовка
        manager.findTaskById(idTask);
        manager.findEpicById(idEpic);
        manager.findSubTaskById(idSubTask);
        manager.findSubTaskById(idSubTask2);
        manager.findSubTaskById(idSubTask);

        //Исполнение
        manager.deleteSubTask(idSubTask);
        final int quantity = 3;
        final List<Task> listHistory = manager.getHistory();
        final int value = listHistory.size();

        //Проверка
        assertEquals(quantity, listHistory.size());
        assertFalse(listHistory.contains(subTask), "Список должен не содержать subTask");
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