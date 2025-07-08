package manager.manager;

import manager.Tasks.Task;

import java.util.Objects;

public class Node {
    public Task data;
    public Node next;
    public Node prev;



    public Node() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return Objects.equals(data, node.data);
    }

    @Override
    public String toString() {
        return "Node{data=" + data + "}";
    }

    public Node(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
