package manager.manager;

import manager.Tasks.Task;
import manager.Tasks.SubTask;
import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.exception.TimeVerificationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HistoryManager historyManager = Managers.getDefaultHistory();
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epical = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int counter = 1;

    //Методы Таска
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
            Task task2 = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getDuration(),
                    task.getStartTime());
            task2.setId(task.getId());
            historyManager.add(task2);
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public int createTask(Task task) {
        task.setId(nextId());
        Task taskNew = new Task(task.getName(), task.getDescription(),
                task.getStatus(), task.getDuration(), task.getStartTime());
        taskNew.setId(task.getId());
        if (taskNew.getEndTime() != null) {
            try {
                intersectionСheck(taskNew);
                prioritizedTasks.add(taskNew);
                tasks.put(taskNew.getId(), taskNew);
            } catch (TimeVerificationException e) {
                System.out.println(e.getMessage());
            }
        } else tasks.put(taskNew.getId(), taskNew);
        return task.getId();
    }

    @Override
    public Task updateTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            Task oldTask = tasks.get(id);
            Task existingTask = new Task(task.getName(), task.getDescription(), task.getStatus(),
                    task.getDuration(), task.getStartTime());
            existingTask.setId(id);
            task.setId(id);
            if (oldTask.getEndTime() != null) {
                try {
                    timeVerification(existingTask);
                    try {
                        prioritizedTasks.remove(oldTask);
                        intersectionСheck(existingTask);
                        prioritizedTasks.add(existingTask);
                        tasks.put(existingTask.getId(), existingTask);
                    } catch (TimeVerificationException e) {
                        System.out.println(e.getMessage());
                        prioritizedTasks.add(oldTask);
                    }
                } catch (TimeVerificationException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                tasks.put(existingTask.getId(), existingTask);
            }
            return task;
        }
        return null;
    }

    @Override
    public Task deleteTask(Integer id) {
        historyManager.remove(id);
        Task task = tasks.remove(id);
        prioritizedTasks.remove(task);
        return task;
    }

    //Методы Эпика
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
            epic1.setStartTime(epic.getStartTime());
            epic1.setDuration(epic.getDuration());
            epic1.setEndTime(epic.getEndTime());
            epic.getSubTasksIds().stream()
                    .filter(Objects::nonNull)
                    .forEach(integer -> epic1.getSubTasksIds().add(integer));
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
            epic.setId(id);
            return epic;
        }
        return null;
    }

    @Override
    public List<SubTask> findAllEpicSubtasks(Epic epic) {
        if (!epical.containsKey(epic.getId())) {
            return Collections.emptyList();
        }
        return epical.get(epic.getId()).getSubTasksIds().stream()
                .filter(Objects::nonNull)
                .map(subTask -> subTasks.get(subTask))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic epic = epical.remove(id);
        if (epic == null) {
            return null;
        }
        epic.getSubTasksIds().stream()
                .filter(Objects::nonNull)
                .forEach(subTaskId -> {
                    historyManager.remove(subTaskId);
                    Task task = subTasks.get(subTaskId);
                    prioritizedTasks.remove(task);
                    historyManager.remove(epic.getId());
                    subTasks.remove(subTaskId);
                });
        return epic;
    }

    //Методы СабТаска
    @Override
    public List<SubTask> findAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllSubTask() {
        subTasks.forEach((id, value) -> {
            idTasksDeleted(subTasks);
            epical.get(value.getEpicId()).getSubTasksIds().remove(id);
            updateStatusEpic(value.getEpicId());
            updateTimeEpic(value.getEpicId());
        });
        subTasks.clear();
    }

    @Override
    public SubTask findSubTaskById(Integer id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            SubTask subTask1 = new SubTask(subTask.getName(), subTask.getDescription(),
                    subTask.getStatus(), subTask.getDuration(), subTask.getStartTime(), subTask.getEpicId());
            subTask1.setId(subTask.getId());
            historyManager.add(subTask1);
            return subTask;
        }
        return null;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        subTask.setId(nextId());
        SubTask subTaskNew = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus(),
                subTask.getDuration(), subTask.getStartTime(), subTask.getEpicId());
        subTaskNew.setId(subTask.getId());
        Epic epic = epical.get(subTaskNew.getEpicId());
        if (epic != null) {
            if (subTaskNew.getEndTime() != null) {
                try {
                    intersectionСheck(subTaskNew);
                    epic.getSubTasksIds().add(subTaskNew.getId());
                    subTasks.put(subTaskNew.getId(), subTaskNew);
                    updateStatusEpic(epic.getId());
                    updateTimeEpic(epic.getId());
                    prioritizedTasks.add(subTaskNew);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                epic.getSubTasksIds().add(subTaskNew.getId());
                subTasks.put(subTaskNew.getId(), subTaskNew);
                updateStatusEpic(epic.getId());
                updateTimeEpic(epic.getId());
            }
        }
        return subTask.getId();
    }

    @Override
    public SubTask updateSubTask(int id, SubTask subTask) {
        if (subTasks.containsKey(id)) {
            SubTask oldSubTask = subTasks.get(id);
            SubTask updateSubtask = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getStatus(),
                    subTask.getDuration(), subTask.getStartTime(), subTask.getEpicId());
            updateSubtask.setId(id);
            subTask.setId(id);
            try {
                timeVerification(updateSubtask);
                try {
                    Optional<SubTask> subTasOpti = prioritizedTasks.stream()
                            .filter(SubTask.class::isInstance)
                            .map(SubTask.class::cast)
                            .filter(subTask1 -> subTask1.equals(oldSubTask))
                            .findFirst();
                    if (!subTasOpti.isEmpty()) {
                        prioritizedTasks.remove(subTasOpti.orElse(null));
                    }
                    intersectionСheck(updateSubtask);
                    subTasks.put(updateSubtask.getId(), updateSubtask);
                    updateStatusEpic(updateSubtask.getEpicId());
                    updateTimeEpic(updateSubtask.getEpicId());
                    prioritizedTasks.add(updateSubtask);
                } catch (TimeVerificationException e) {
                    System.out.println(e.getMessage());
                    prioritizedTasks.add(oldSubTask);
                }
            } catch (TimeVerificationException e) {
                System.out.println(e.getMessage());
            }
            return subTask;
        }
        return null;
    }

    @Override
    public SubTask deleteSubTask(Integer id) {
        if (!subTasks.containsKey(id)) {
            return null;
        }
        historyManager.remove(id);
        SubTask subTask = subTasks.get(id);
        Epic epic = epical.get(subTask.getEpicId());
        epic.getSubTasksIds().remove(subTask.getId());
        updateStatusEpic(epic.getId());
        updateTimeEpic(epic.getId());
        prioritizedTasks.remove(subTask);
        return subTasks.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private int nextId() {
        return counter++;
    }

    //Удаление всех задач из истории и множества
    private void idTasksDeleted(Map<Integer, ? extends Task> task) {
        task.forEach((id, value) -> {
            historyManager.remove(id);
            if (value.getEndTime() != null) {
                prioritizedTasks.remove(value);
            }
        });
    }

    //Метод обновления времени Epic
    private void updateTimeEpic(Integer id) {
        Epic existingEpic = epical.get(id);
        List<SubTask> epicSubTaskList = existingEpic.getSubTasksIds().stream()
                .filter(Objects::nonNull)
                .map(idSubTask -> subTasks.get(idSubTask))
                .filter(Objects::nonNull)
                .toList();

        if (!epicSubTaskList.isEmpty()) {
            LocalDateTime minTime = epicSubTaskList.stream()
                    .filter(subTask -> subTask.getEndTime() != null)
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            existingEpic.setStartTime(minTime);

            LocalDateTime maxTime = epicSubTaskList.stream()
                    .filter(subTask -> subTask.getEndTime() != null)
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            existingEpic.setEndTime(maxTime);

            Duration duration = epicSubTaskList.stream()
                    .map(Task::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration::plus)
                    .orElse(null);

            existingEpic.setDuration(duration);
        } else {
            existingEpic.setStartTime(null);
            existingEpic.setEndTime(null);
            existingEpic.setDuration(null);
        }
    }

    //Метод обновления статуса Эпика
    private void updateStatusEpic(Integer id) {
        Epic epic = epical.get(id);
        if (epic == null) {
            return;
        }
        List<SubTask> subTaskList = epic.getSubTasksIds().stream()
                .map(integer -> subTasks.get(integer))
                .toList();

        if (subTaskList.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            Boolean allStatusDone = subTaskList.stream()
                    .allMatch(subTask -> subTask.getStatus() == Status.DONE);

            Boolean anyStatusProgress = subTaskList.stream()
                    .anyMatch(subTask -> subTask.getStatus() == Status.IN_PROGRESS);

            boolean anyStatusDone = subTaskList.stream()
                    .anyMatch(subTask -> subTask.getStatus() == Status.DONE);

            epical.get(id).setStatus(allStatusDone ? Status.DONE :
                    anyStatusProgress ? Status.IN_PROGRESS : anyStatusDone ? Status.IN_PROGRESS : Status.NEW);
        }
    }

    //Метод проверяет задачи на пересечение по времени
    private void intersectionСheck(Task task) {
        boolean anyMatch = prioritizedTasks.stream()
                .anyMatch(taskStream -> task.getStartTime().isBefore(taskStream.getEndTime()) &&
                        task.getEndTime().isAfter(taskStream.getStartTime()));
        if (anyMatch) {
            String errorMessage = "Найдено пересечение задачь " + task.toString();
            throw new TimeVerificationException(errorMessage);
        }
    }

    //Метод проверяет полноту временных параметров задачи
    private void timeVerification(Task task) {
        if (task.getEndTime() == null) {
            String errorMessage = "Неполные данные по времени " + task.toString();
            throw new TimeVerificationException(errorMessage);
        }
    }
}
