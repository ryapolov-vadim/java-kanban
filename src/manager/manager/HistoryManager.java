package manager.manager;

import manager.Tasks.Task;

import java.util.List;

public interface HistoryManager {
    <T extends Task> void add(T task);

    List<Task> getBrowsingHistory();
}
