package manager.Tasks;

import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private Status status;
    private static int counter = 0;

    public Task(Integer id, String name, String description, Status status) {
        if (id == null) {
            this.id = ++counter;
        } else {
            this.id = id;
        }
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String name, String description) {
        //конструктор для Epic
        if (id == null) {
            this.id = ++counter;
        } else {
            this.id = id;
        }
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
