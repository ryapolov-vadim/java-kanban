package manager.manager;

import manager.Tasks.Task;
import manager.Tasks.SubTask;
import manager.Tasks.Epic;
import manager.Tasks.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epical = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    public List<Task> findAllTasks() {
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
        return tasks.remove(id);
    }
    //реализация Epic

    public List<Epic> findAllEpic() {
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
            existingEpic.setSubTasksIds(epic.getSubTasksIds());

            boolean allDone = true;
            Status newEpicStatus = Status.NEW;
            for (Integer subTaskId : existingEpic.getSubTasksIds()) {
                SubTask subTask = subTasks.get(subTaskId);
                if (subTask.getStatus() == Status.IN_PROGRESS) {
                    newEpicStatus = Status.IN_PROGRESS;
                }
                if (subTask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
            if (allDone) {
                newEpicStatus = Status.DONE;
            }
            existingEpic.setStatus(newEpicStatus);
            return existingEpic;
        }
        return null;
    }

    public Epic deleteEpic(Integer id) {
        //удаление задачи, возвращение удалённой задачи
        return epical.remove(id);
    }

    //реализация SubTask

    public List<SubTask> findAllSubTask() {
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
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }


    //все подзадачи должны знать к какому manager.Tasks.Epic они относятся,
    //а каждый manager.Tasks.Epic должен знать, какие подзадачи в него входят
}
