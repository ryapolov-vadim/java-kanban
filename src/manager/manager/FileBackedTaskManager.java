package manager.manager;

import manager.Tasks.Epic;
import manager.Tasks.Status;
import manager.Tasks.SubTask;
import manager.Tasks.Task;
import manager.exception.ManagerFileInitializationException;
import manager.exception.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public List<Task> findAllTasks() {
        return super.findAllTasks();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public Task findTaskById(Integer id) {
        return super.findTaskById(id);
    }

    @Override
    public int createTask(Task task) {
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public Task updateTask(int id, Task task) {
        Task task1 = super.updateTask(id, task);
        save();
        return task1;
    }

    @Override
    public Task deleteTask(Integer id) {
        Task task = super.deleteTask(id);
        save();
        return task;
    }

    @Override
    public List<Epic> findAllEpic() {
        return super.findAllEpic();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Epic findEpicById(Integer id) {
        return super.findEpicById(id);
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public Epic updateEpic(int id, Epic epic) {
        Epic epic1 = super.updateEpic(id, epic);
        save();
        return epic1;
    }

    @Override
    public List<SubTask> findAllEpicSubtasks(Epic epic) {
        return super.findAllEpicSubtasks(epic);
    }

    @Override
    public Epic deleteEpic(Integer id) {
        Epic epic1 = super.deleteEpic(id);
        save();
        return epic1;
    }

    @Override
    public List<SubTask> findAllSubTask() {
        return super.findAllSubTask();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public SubTask findSubTaskById(Integer id) {
        return super.findSubTaskById(id);
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int subTaskId = super.createSubTask(subTask);
        save();
        return subTaskId;
    }

    @Override
    public SubTask updateSubTask(int id, SubTask subTask) {
        SubTask subTask1 = super.updateSubTask(id, subTask);
        save();
        return subTask1;
    }

    @Override
    public SubTask deleteSubTask(Integer id) {
        SubTask subTask = super.deleteSubTask(id);
        save();
        return subTask;
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new ManagerFileInitializationException("Ошибка инициализации файла: файл пустой");
        }
        List<Task> taskList;
        try {
            FileBackedTaskManager fBTaskManager = new FileBackedTaskManager(file);
            try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                taskList = stream
                        .map(fBTaskManager::fromString)
                        .filter(Objects::nonNull)
                        .toList();
            }

            taskList.stream()
                    .filter(SubTask.class::isInstance)
                    .map(SubTask.class::cast)
                    .forEach(subTask -> taskList.stream()
                            .filter(Epic.class::isInstance)
                            .map(Epic.class::cast)
                            .filter(epic -> subTask.getEpicId() == epic.getId())
                            .forEach(epic -> epic.getSubTasksIds().add(subTask.getId())));

            taskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(task -> {
                                if (task instanceof SubTask) {
                                    fBTaskManager.subTasks.put(task.getId(), (SubTask) task);
                                } else if (task instanceof Epic) {
                                    fBTaskManager.epical.put(task.getId(), (Epic) task);
                                } else {
                                    fBTaskManager.tasks.put(task.getId(), task);
                                }
                            }
                    );
            fBTaskManager.counter = taskList.size() + 1;

            return fBTaskManager;
        } catch (IOException e) {
            String errorMessage = "Ошибка при загрузке менеджера";
            throw new ManagerFileInitializationException(errorMessage + e.getMessage());
        }
    }

    private void save() {
        List<Task> allTasks = findAllTasks();
        List<Epic> allEpics = findAllEpic();
        List<SubTask> allSubTasks = findAllSubTask();
        allTasks.addAll(allEpics);
        allTasks.addAll(allSubTasks);

        try {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
                bw.write("id,type,name,status,description,epic,duration,startTime,endTime\n");
            }

            for (Task task : allTasks) {
                String taskAsString = toString(task);
                writeStringTofile(taskAsString);
            }

        } catch (IOException e) {
            String errorMessage = "Ошибка при записи в файл " + e.getMessage();
            throw new ManagerSaveException(errorMessage);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",").append(task.getType()).append(",").append(task.getName()).append(",")
                .append(task.getStatus()).append(",").append(task.getDescription());
        StringBuilder epic = new StringBuilder();
        StringBuilder duration = new StringBuilder();
        StringBuilder startTime = new StringBuilder();
        StringBuilder endTime = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

        if (task.getDuration() != null) duration.append(task.getDuration().toMinutes());
        if (task.getStartTime() != null) startTime.append(task.getStartTime().format(dtf));
        if ((task instanceof SubTask)) epic.append(((SubTask) task).getEpicId());
        if (task.getEndTime() != null) endTime.append(task.getEndTime().format(dtf));

        if (task.getStartTime() == null && task.getDuration() == null) {
            return String.format("%s,%s\n", sb, epic);
        } else if (task.getDuration() == null && task.getStartTime() != null) {
            return String.format("%s,%s,,%s\n", sb, epic, startTime);
        } else if (task.getDuration() != null && task.getStartTime() == null) {
            return String.format("%s,%s,%s\n", sb, epic, duration);
        } else {
            return String.format("%s,%s,%s,%s,%s\n", sb, epic, duration,
                    startTime, endTime);
        }
    }

    private void writeStringTofile(String taskAsString) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            bw.write(taskAsString);
        } catch (IOException e) {
            String errorMessage = "Ошибка при записи в файл " + e.getMessage();
            throw new ManagerSaveException(errorMessage);
        }
    }

    // Метод создания Задач
    private Task fromString(String value) {
        String[] taskLine = value.split(",");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");

        switch (taskLine[1]) {
            case "TASK":
                Task task = new Task(taskLine[2], taskLine[4], Status.valueOf(taskLine[3]));
                task.setId(Integer.parseInt(taskLine[0]));
                if (taskLine.length > 6 && (!taskLine[6].isBlank()))
                    task.setDuration(Duration.ofMinutes(Integer.parseInt(taskLine[6])));
                if (taskLine.length > 7 && (!taskLine[7].isBlank()))
                    task.setStartTime(LocalDateTime.parse(taskLine[7], dtf));
                return task;

            case "EPIC":
                Epic epic = new Epic(taskLine[2], taskLine[4]);
                epic.setId(Integer.parseInt(taskLine[0]));
                epic.setStatus(Status.valueOf(taskLine[3]));
                if (taskLine.length > 6 && (!taskLine[6].isBlank()))
                    epic.setDuration(Duration.ofMinutes(Integer.parseInt(taskLine[6])));
                if (taskLine.length > 7 && (!taskLine[7].isBlank())) {
                    epic.setStartTime(LocalDateTime.parse(taskLine[7], dtf));
                    epic.setEndTime(LocalDateTime.parse(taskLine[8], dtf));
                }
                return epic;

            case "SUBTASK":
                SubTask subTask = new SubTask(taskLine[2], taskLine[4], Status.valueOf(taskLine[3]),
                        Integer.parseInt(taskLine[5]));
                subTask.setId(Integer.parseInt(taskLine[0]));
                if (taskLine.length > 6 && (!taskLine[6].isBlank()))
                    subTask.setDuration(Duration.ofMinutes(Integer.parseInt(taskLine[6])));
                if (taskLine.length > 7 && (!taskLine[7].isBlank()))
                    subTask.setStartTime(LocalDateTime.parse(taskLine[7], dtf));
                return subTask;
        }
        return null;
    }
}
