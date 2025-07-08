package manager.manager;

import manager.Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private Map<Integer, Node> browsingHistory = new HashMap<>();


    private void linkLast(Task task) {
        Node newNode = new Node(task);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node node = head;
        while (node != null) {
            Task task = node.data;
            list.add(task);
            node = node.next;
        }
        return list;
    }

    private void removeNode(Node node) {
        if (node != null) {
            if ((node.prev != null) && (node.next != null)) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
            }
            if ((node == head) && (node.next != null)) {
                head = node.next;
                head.prev = null;
            }
            if ((node == tail) && (node.prev != null)) {
                tail = node.prev;
                tail.next = null;
            }
            if ((node == head) && (node == tail)) {
                head = null;
                tail = null;
            }
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (browsingHistory.containsKey(task.getId())) {

            removeNode(browsingHistory.get(task.getId()));
        }
        linkLast(task);
        browsingHistory.put(task.getId(), this.tail);
    }

    @Override
    public void remove(int id) {
        if (browsingHistory.get(id) == null) {
            return;
        } else {
            Node node = browsingHistory.get(id);
            removeNode(node);
            browsingHistory.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
