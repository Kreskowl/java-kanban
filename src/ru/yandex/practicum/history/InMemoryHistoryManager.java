package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> history = new LinkedList<>();
    private final static int MAX_SIZE = 10;

    @Override
    public void add(Task task){
        if(isFull()){
            history.remove(0);
            history.add(history.size(),task);
        } else {
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory(){
        return new LinkedList<>(history);
    }

    private boolean isFull() {
        if (history.size() >= MAX_SIZE) {
            return true;
        } else {
            return false;
        }
    }
}
