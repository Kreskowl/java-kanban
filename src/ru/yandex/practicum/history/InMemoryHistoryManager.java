package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private List<Task> history = new ArrayList<>();

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
        return new ArrayList<>(history);
    }

    @Override
    public void clearHistory() {
        history.clear();
    }
    private boolean isFull() {
        if (history.size() > 9) {
            return true;
        } else {
            return false;
        }
    }
}
