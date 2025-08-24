package manager.Tasks;

import manager.manager.TaskType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    List<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasksIds = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (endTime != null) {
            return endTime;
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "endTime=" + endTime + "," + "subTasksIds=" + subTasksIds +
                '}';
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
