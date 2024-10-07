package com.yandex.add.service;

import com.yandex.add.model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> historyMap;
    private Node head;
    private Node tail;

    public int getHistorySize() {
        return this.historyMap.size();
    }

    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        remove(task.getIdNum());
        linkLast(task);
        historyMap.put(task.getIdNum(), tail);
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
        // Реализация метода getHistory должна перекладывать задачи из связного списка в ArrayList для формирования ответа.
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
        if (node.prev != null) {
            final Node next = node.next; // здесь мод.private недоступен(?)
            final Node prev = node.prev;
            prev.next = next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;

    }

    private List<Task> getTasks() {
        List<Task> tasks = new LinkedList<>();
        Node currentHead = head;
        while (currentHead != null) {
            tasks.add(currentHead.data);
            currentHead = currentHead.next;
        }
        return tasks;
        //собирать все задачи из него в обычный ArrayList
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









