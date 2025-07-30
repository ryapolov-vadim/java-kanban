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
import java.util.ArrayList;
import java.util.List;

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

    public static FileBackedTaskManager loadFromFile(File file) {
        List<Task> taskList = new ArrayList<>();
        try {
            FileBackedTaskManager fBTaskManager = new FileBackedTaskManager(file);
            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    Task task = fBTaskManager.fromString(line);
                    taskList.add(task);
                }
                if (taskList.contains(null)) {
                    taskList.remove(null);
                }
            }

            for (Task task : taskList) {
                if (task instanceof SubTask) {
                    SubTask subTask = (SubTask) task;
                    for (Task task1 : taskList) {
                        if (task1 instanceof Epic) {
                            Epic epic1 = (Epic) task1;
                            if (subTask.getEpicId() == epic1.getId()) {
                                epic1.getSubTasksIds().add(subTask.getId());
                            }
                        }
                    }
                }
            }

            for (Task task : taskList) {
                if (task instanceof SubTask) {
                    SubTask subTask = (SubTask) task;
                    fBTaskManager.subTasks.put(subTask.getId(), subTask);
                } else if (task instanceof Epic) {
                    Epic epic = (Epic) task;
                    fBTaskManager.epical.put(epic.getId(), epic);
                } else {
                    fBTaskManager.tasks.put(((Task) task).getId(), task);
                }
            }
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
            Files.deleteIfExists(file.toPath());
            Files.createFile(file.toPath());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
                bw.write("id,type,name,status,description,epic\n");
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
        StringBuilder id = new StringBuilder();
        StringBuilder type = new StringBuilder();
        StringBuilder name = new StringBuilder();
        StringBuilder status = new StringBuilder();
        StringBuilder description = new StringBuilder();
        StringBuilder epic = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            if (i == 0) id.append(task.getId());
            if (i == 1) name.append(task.getName());
            if (i == 2) description.append(task.getDescription());
            if (i == 3) status.append(task.getStatus());
            if ((i == 4) && (task instanceof SubTask)) epic.append(((SubTask) task).getEpicId());
        }
        if (task instanceof Epic) {
            type.append(TaskType.EPIC);
        } else if (task instanceof SubTask) {
            type.append(TaskType.SUBTASK);
        } else {
            type.append(TaskType.TASK);
        }

        return String.format("%s,%s,%s,%s,%s,%s\n", id, type, name, status, description, epic);
    }

    private void writeStringTofile(String taskAsString) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            bw.write(taskAsString);
        } catch (IOException e) {
            String errorMessage = "Ошибка при записи в файл " + e.getMessage();
            throw new ManagerSaveException(errorMessage);
        }
    }

    private Task fromString(String value) {
        String[] taskLine = value.split(",");

        switch (taskLine[1]) {
            case "TASK":
                Task task = new Task(taskLine[2], taskLine[4], Status.valueOf(taskLine[3]));
                task.setId(Integer.parseInt(taskLine[0]));
                return task;

            case "EPIC":
                Epic epic = new Epic(taskLine[2], taskLine[4]);
                epic.setId(Integer.parseInt(taskLine[0]));
                epic.setStatus(Status.valueOf(taskLine[3]));
                return epic;

            case "SUBTASK":
                SubTask subTask = new SubTask(taskLine[2], taskLine[4], Status.valueOf(taskLine[3]),
                        Integer.parseInt(taskLine[5]));
                subTask.setId(Integer.parseInt(taskLine[0]));
                return subTask;
        }
        return null;
    }

}
