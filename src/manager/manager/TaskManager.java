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

    //методы Таска
    public List<Task> findAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public Task findTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Task createTask(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }
        return task;
    }

    public Task deleteTask(Integer id) {
        return tasks.remove(id);
    }

    //методы Эпика

    public List<Epic> findAllEpic() {
        return new ArrayList<>(epical.values());
    }

    public void deleteAllEpic() {
        epical.clear();
    }

    public Epic findEpicById(Integer id) {
        if (epical.containsKey(id)) {
            return epical.get(id);
        }
        return null;
    }

    public Epic createEpic(Epic epic) {
        //epic.setStatus(Status.NEW);
        epical.put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        if (epical.containsKey(epic.getId())) {
            Epic existingEpic = epical.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            existingEpic.setSubTasksIds(epic.getSubTasksIds());
        }
        return null;
    }

    public List<SubTask> findAllEpicSubtasks(Epic epic) {
        List<SubTask> allSubtasks = new ArrayList<>();
        for (Integer subTaskId : epic.getSubTasksIds()) {
            SubTask subTask = subTasks.get(subTaskId);
            allSubtasks.add(subTask);
        }
        return allSubtasks;
    }

    public Epic deleteEpic(Integer id) {
        Epic epic = epical.get(id);
        for (Integer idSubTasc : epic.getSubTasksIds()) {
            subTasks.remove(idSubTasc);
        }
        return epical.remove(id);
    }

    //методы СабТаска

    public List<SubTask> findAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllSubTask() {
        subTasks.clear();
    }

    public SubTask findSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {

            return subTasks.get(id);
        }
        return null;
    }

    public SubTask createSubTask(SubTask subTask) {
        //добавление id в Epic
        Epic epic = epical.get(subTask.getEpicId());
        subTasks.put(subTask.getId(), subTask);
        if (epic != null) {
            if(epic.getSubTasksIds() == null) {
                epic.setSubTasksIds(new ArrayList<>());
            }
            epic.getSubTasksIds().add(subTask.getId());
        }
        return subTask;
    }

    public SubTask updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            SubTask existingSubtask = subTasks.get(subTask.getId());
            existingSubtask.setName(subTask.getName());
            existingSubtask.setDescription(subTask.getDescription());
            existingSubtask.setStatus(subTask.getStatus());
            Epic existingEpic = epical.get(subTask.getEpicId());

            boolean allDone = true;
            Status newEpicStatus = Status.NEW;
            for (Integer subTaskId : existingEpic.getSubTasksIds()) {
                SubTask idSubTask = subTasks.get(subTaskId);
                if (idSubTask.getStatus() == Status.IN_PROGRESS) {
                    newEpicStatus = Status.IN_PROGRESS;
                }
                if (idSubTask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }
            if (allDone) {
                newEpicStatus = Status.DONE;
            }
            existingEpic.setStatus(newEpicStatus);
        }
        return subTask;
    }

    public SubTask deleteSubTask(Integer id) {
        return subTasks.remove(id);
    }
}
