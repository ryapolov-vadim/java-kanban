package manager.manager;

import manager.Tasks.Task;
import manager.Tasks.SubTask;
import manager.Tasks.Epic;
import manager.Tasks.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epical = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int counter = 0;

    private int nextId() {
        return counter++;
    }

    public ArrayList<manager.Tasks.Task> findeAllTasks() {
        //возврат списка всех задач
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public Task findTaskById(Integer id) {
        //поиск задачи по id, возврат найденной задачи (либо null)
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Task createTask(Task task) {
        //генерация id, сохранение в Map, возврат задачи с id
        int newId = nextId();
        task.setId(newId);
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        //проверка наличия задачи, обновление, возврат обновлённой задачи
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }
        return task;
    }

    public Task deleteTask(Integer id) {
        //удаление задачи, возвращение удалённой задачи
        Task task = tasks.remove(id);
        return task;
    }
    //реализация Epic

    public ArrayList<manager.Tasks.Epic> findeAllEpic() {
        //возврат списка всех задач
        return new ArrayList<>(epical.values());
    }

    public void deleteAllEpic() {
        epical.clear();
    }

    public Epic findEpicById(Integer id) {
        //поиск задачи по id, возврат найденной задачи (либо null)
        if (epical.containsKey(id)) {
            return epical.get(id);
        }
        return null;
    }

    public Epic createEpic(Epic epic) {
        //генерация id, сохранение в Map, возврат задачи с id
        int newId = nextId();
        epic.setId(newId);
        epic.setStatus(Status.NEW);
        epical.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        //проверка наличия задачи, обновление, возврат обновлённой задачи
        if (epical.containsKey(epic.getId())) {
            Epic existingEpic = epical.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            existingEpic.setSubTaskIds(epic.getSubTaskIds());

            for (Integer idSubtask : existingEpic.getSubTaskIds())
                for (SubTask subTask : subTasks.values()) {
                    if (idSubtask == subTask.getEpicId()) {
                        if (subTask.getStatus() == Status.DONE) {
                            existingEpic.setStatus(Status.DONE);
                        } else if (subTask.getStatus() == Status.IN_PROGRESS) {
                            existingEpic.setStatus(Status.IN_PROGRESS);
                        } else {
                            epic.setStatus(Status.NEW);
                        }
                    }
                }
        }
        return epical.get(epic.getId());
    }

    public Epic deleteEpic(Integer id) {
        //удаление задачи, возвращение удалённой задачи
        Epic epic = epical.remove(id);
        return epic;
    }

    //реализация SubTask

    public ArrayList<manager.Tasks.SubTask> findeAllSubTask() {
        //возврат списка всех задач
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllSubTask() {
        subTasks.clear();
    }

    public SubTask findSubTaskById(Integer id) {
        //поиск задачи по id, возврат найденной задачи (либо null)
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    public SubTask createSubTask(SubTask subTask) {
        //генерация id, сохранение в Map, возврат задачи с id
        int newId = nextId();
        subTask.setId(newId);
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }


    //все подзадачи должны знать к какому manager.Tasks.Epic они относятся,
    //а каждый manager.Tasks.Epic должен знать, какие подзадачи в него входят
}
