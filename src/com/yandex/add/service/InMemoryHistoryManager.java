package com.yandex.add.service;

import com.yandex.add.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
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

    private Map<Integer, Node> historyMap;
    private final static int HISTORY_LENGTH = 10;
    private Node head;
    private Node tail;

    public int getMapSize() {
        return this.historyMap.size();

    }

    public InMemoryHistoryManager() {
        this.historyMap = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    //Todo links
    @Override
    public void add(Task task) {
        if (task == null) return;
        if (historyMap.get(task.getIdNum()) != null) {
            removeNode(historyMap.get(task.getIdNum()));
            historyMap.remove(task.getIdNum());
        }
        if (historyMap.size() == HISTORY_LENGTH) {
            removeNode(head);
            historyMap.remove(head.data.getIdNum());
        }
        if (tail == null) {
            tail = new Node(null, task, null);
            head = tail;
        } else {
            linkLast(task);
        }
        historyMap.put(task.getIdNum(), tail);
    }

    @Override
    public void remove(int id) {
        Node toRemove = historyMap.get(id);
        if (toRemove != null) {
            historyMap.remove(toRemove.data.getIdNum());
            removeNode(toRemove);
        } else {
            System.out.println("task id " + id + " not found");
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
        // Реализация метода getHistory должна перекладывать задачи из связного списка в ArrayList для формирования ответа.
    }

    //ToDo links
    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newTail = new Node(oldTail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
            newTail.prev = oldTail;
        }
        //будет добавлять задачу в конец этого списка
    }

    private void removeNode(Node node) {
        if (node == null) return;
        historyMap.remove(node.data.getIdNum());

        if (node.prev != null) {
            node.prev.next = node.next;
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
        List<Task> tasks = new ArrayList<>();
        Node currentHead = head;
        while (currentHead != null) {
            tasks.add(currentHead.data);
            currentHead = currentHead.next;
        }
        return tasks;
        //собирать все задачи из него в обычный ArrayList
    }


}









