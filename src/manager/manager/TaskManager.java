package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.SubTask;
import manager.Tasks.Task;

import java.util.List;

public interface TaskManager {

    List<Task> findAllTasks();

    void deleteAllTask();

    Task findTaskById(Integer id);

    int createTask(Task task);

    Task updateTask(int id, Task task);

    Task deleteTask(Integer id);

    List<Epic> findAllEpic();

    void deleteAllEpic();

    Epic findEpicById(Integer id);

    int createEpic(Epic epic);

    Epic updateEpic(int id, Epic epic);

    List<SubTask> findAllEpicSubtasks(Epic epic);

    Epic deleteEpic(Integer id);

    List<SubTask> findAllSubTask();

    void deleteAllSubTask();

    SubTask findSubTaskById(Integer id);

    int createSubTask(SubTask subTask);

    SubTask updateSubTask(int id, SubTask subTask);

    SubTask deleteSubTask(Integer id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
