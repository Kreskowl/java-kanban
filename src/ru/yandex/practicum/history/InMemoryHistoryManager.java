package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private final HashMap<Integer, Node<Task>> history = new HashMap<>();


    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = history.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Task task) {
        Node<Task> node = new Node<>(tail, task, null);
        if (tail != null) {
            tail.next = node;
        }
        tail = node;

        if (head == null) {
            head = node;
        }
        history.put(task.getId(), node);
    }

   private List<Task> getTasks() {
        List<Task> result = new ArrayList<>();
        Node<Task> actual = head;
        while (actual != null) {
            result.add(actual.data);
            actual = actual.next;
        }
        return result;
    }

    private void removeNode(Node<Task> node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }
        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }
    }
}
