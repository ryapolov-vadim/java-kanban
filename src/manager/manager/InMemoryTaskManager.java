package manager.manager;

import manager.Tasks.Task;
import manager.Tasks.SubTask;
import manager.Tasks.Epic;
import manager.Tasks.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private HistoryManager historyManager = Managers.getDefaultHistory();
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epical = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected int counter = 1;

    //методы Таска
    @Override
    public List<Task> findAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTask() {
        idTasksDeleted(tasks);
        tasks.clear();
    }

    @Override
    public Task findTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            Task task2 = new Task(task.getName(), task.getDescription(), task.getStatus());
            task2.setId(task.getId());
            historyManager.add(task2);
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public int createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        Task task1 = new Task(task.getName(), task.getDescription(), task.getStatus());
        task1.setId(task.getId());
        tasks.put(task1.getId(), task1);
        return task.getId();
    }

    @Override
    public Task updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            Task existingTask = tasks.get(id);
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
            task.setId(id);
            return task;
        }
        return null;
    }

    @Override
    public Task deleteTask(Integer id) {
        historyManager.remove(id);
        return tasks.remove(id);
    }

    //методы Эпика

    @Override
    public List<Epic> findAllEpic() {
        return new ArrayList<>(epical.values());
    }

    @Override
    public void deleteAllEpic() {
        idTasksDeleted(epical);
        idTasksDeleted(subTasks);
        epical.clear();
        subTasks.clear();
    }

    @Override
    public Epic findEpicById(Integer id) {
        if (epical.containsKey(id)) {
            Epic epic = epical.get(id);
            Epic epic1 = new Epic(epic.getName(), epic.getDescription());
            epic1.setId(epic.getId());
            epic1.setStatus(epic.getStatus());
            historyManager.add(epic1);
            return epical.get(id);
        }
        return null;
    }

    @Override
    public int createEpic(Epic epic) {
        int newId = nextId();
        epic.setId(newId);
        Epic epic1 = new Epic(epic.getName(), epic.getDescription());
        epic1.setId(epic.getId());
        epical.put(epic1.getId(), epic1);
        return epic.getId();
    }

    @Override
    public Epic updateEpic(int id, Epic epic) {
        if (epical.containsKey(id)) {
            Epic existingEpic = epical.get(id);
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
            updateStatusEpic(id);
            epic.setId(id);
            return epic;
        }
        return null;
    }

    @Override
    public List<SubTask> findAllEpicSubtasks(Epic epic) {
        List<SubTask> allSubtasks = new ArrayList<>();
        if (epical.containsKey(epic.getId())) {
            for (Integer subTaskId : epical.get(epic.getId()).getSubTasksIds()) {
                SubTask subTask = subTasks.get(subTaskId);
                if (subTask == null) {
                    return allSubtasks;
                }
                allSubtasks.add(subTask);
            }
        }
        return allSubtasks;
    }

    @Override
    public Epic deleteEpic(Integer id) {
        historyManager.remove(id);
        for (Integer i : epical.get(id).getSubTasksIds()) {
            historyManager.remove(i);
        }
        Epic epic = epical.get(id);
        for (Integer idSubTasc : epic.getSubTasksIds()) {
            subTasks.remove(idSubTasc);
        }
        return epical.remove(id);
    }

    //методы СабТаска

    @Override
    public List<SubTask> findAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllSubTask() {
        idTasksDeleted(subTasks);
        for (Integer subTaskId : new ArrayList<>(subTasks.keySet())) {
            SubTask subTask = subTasks.get(subTaskId);
            Epic epic = epical.get(subTask.getEpicId());
            epic.getSubTasksIds().remove(subTask.getId());
            subTasks.remove(subTask.getId());
            updateStatusEpic(epic.getId());
        }
    }

    @Override
    public SubTask findSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            SubTask subTask1 = new SubTask(subTask.getName(), subTask.getDescription(),
                    subTask.getStatus(), subTask.getEpicId());
            subTask1.setId(subTask.getId());
            historyManager.add(subTask1);
            return subTask;
        }
        return null;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int newId = nextId();
        subTask.setId(newId);
        SubTask subTask1 = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus(),
                subTask.getEpicId());
        subTask1.setId(subTask.getId());
        Epic epic = epical.get(subTask1.getEpicId());
        if (epic != null) {
            epic.getSubTasksIds().add(subTask1.getId());
            subTasks.put(subTask1.getId(), subTask1);
            updateStatusEpic(epic.getId());
        }
        return subTask.getId();
    }

    @Override
    public SubTask updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id)) {
            SubTask existingSubtask = subTasks.get(id);
            existingSubtask.setName(subTask.getName());
            existingSubtask.setDescription(subTask.getDescription());
            existingSubtask.setStatus(subTask.getStatus());
            updateStatusEpic(existingSubtask.getEpicId());
            subTask.setId(id);
        }
        return subTask;
    }

    @Override
    public SubTask deleteSubTask(Integer id) {
        if (subTasks.containsKey(id)) {
            historyManager.remove(id);
            SubTask subTask = subTasks.get(id);
            Epic epic = epical.get(subTask.getEpicId());
            epic.getSubTasksIds().remove(subTask.getId());
            updateStatusEpic(epic.getId());
            return subTasks.remove(id);
        }
        return null;
    }

    private void updateStatusEpic(Integer id) {
        Epic existingEpic = epical.get(id);
        boolean allDone = true;
        Status newEpicStatus = Status.NEW;

        if (!existingEpic.getSubTasksIds().isEmpty()) {
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
        } else {
            existingEpic.setStatus(newEpicStatus);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int nextId() {
        return counter++;
    }

    private void idTasksDeleted(Map<Integer, ? extends Task> task) {
        if (!task.isEmpty()) {
            for (Map.Entry<Integer, ? extends Task> entry : task.entrySet()) {
                historyManager.remove(entry.getKey());
            }
        }
    }
}
