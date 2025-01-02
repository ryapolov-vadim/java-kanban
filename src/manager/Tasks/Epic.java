package manager.Tasks;

import java.util.List;

public class Epic extends Task {
    List<Integer> subTasksIds;

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(List<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }
}
