package manager.Tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    ArrayList <Integer> subTaskIds;

    public Epic(Integer id, String name, String description, ArrayList<Integer> subTaskIds) {
        super(id, name, description);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTaskIds, epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskIds);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTaskIds=" + subTaskIds +
                '}';
    }
}
