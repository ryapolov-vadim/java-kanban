package manager.Tasks;

import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    List<Integer> subTasksIds;

    public Epic(Integer id, String name, String description, List<Integer> subTaskIds) {
        super(id, name, description);
        this.subTasksIds = subTaskIds;
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(List<Integer> subTasksIds) {
        this.subTasksIds = subTasksIds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasksIds, epic.subTasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasksIds=" + subTasksIds +
                '}';
    }
}
