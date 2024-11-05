package com.yandex.add.service.history;

import com.yandex.add.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> localHistory;
    private Node head;
    private Node tail;

    public int getHistorySize() {
        return this.localHistory.size();
    }

    public InMemoryHistoryManager() {
        this.localHistory = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getIdNum());
        linkLast(task);
        localHistory.put(task.getIdNum(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(localHistory.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newTail = new Node(oldTail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        //будет добавлять задачу в конец этого списка
    }

    private void removeNode(Node node) {
        if (node == null) return;
        final Node next = node.next;
        final Node prev = node.prev;
        if (prev != null) {
            prev.next = next;
        } else {
            head = next;
        }
        if (next != null) {
            next.prev = prev;
        } else {
            tail = prev;
        }
        node.prev = null;
        node.next = null;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node currentHead = head;
        while (currentHead != null) {
            tasks.add(currentHead.data);
            currentHead = currentHead.next;
        }
        return tasks;
    }

    private static class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task task, Node next) {
            this.data = task;
            this.next = next;
            this.prev = prev;
        }
    }
}










