package manager.manager;

import manager.Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> browsingHistory = new ArrayList<>();
    private int counterHistory = 10;

    @Override
    public void add(Task task) {

        if (counterHistory > browsingHistory.size()) {
            browsingHistory.add(task);
        } else {
            browsingHistory.remove(0);
            browsingHistory.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(browsingHistory);
    }

}
