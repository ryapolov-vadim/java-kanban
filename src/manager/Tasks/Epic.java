package manager.Tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasksIds = new ArrayList<>();
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }
}
