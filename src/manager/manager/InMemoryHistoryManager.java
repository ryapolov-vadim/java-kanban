package manager.manager;

import manager.Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> browsingHistory = new ArrayList<>();
    private int counterHistory = 10;

    @Override
    public <T extends Task> void add(T task) {

        if (counterHistory > browsingHistory.size()) {
            browsingHistory.add(task);
        } else {
            browsingHistory.remove(0);
            browsingHistory.add(task);
        }
    }

    @Override
    public List<Task> getBrowsingHistory() {
        return new ArrayList<>(browsingHistory);
    }

}
