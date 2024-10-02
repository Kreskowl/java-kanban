package ru.yandex.practicum.history;

public class Node<T> {
    T data;
    Node<T> next;
    Node<T> prev;

    Node(Node<T> prev, T task, Node<T> next) {
        this.data = task;
        this.next = next;
        this.prev = prev;
    }
}
