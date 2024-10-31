package ru.yandex.practicum.manager;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Status;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>();
    private final HistoryManager history;
    private final Map<LocalDateTime, Boolean> schedule = new HashMap<>();
    private static final int INTERVAL_MINUTES = 15;
    private static final int PLANNING_PERIOD_YEARS = 1;
    private int currentId = 1;

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
        initializeSchedule();
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    @Override
    public List<Task> getTasksList() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return subTasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpicsList() {
        return epics.values().stream().toList();
    }

    @Override
    public void clearTasks() {
        prioritizedTasks.removeIf(task -> task != null && !(task instanceof SubTask || task instanceof Epic));
        tasks.values().forEach(this::releaseIntervals);
        tasks.clear();
    }

    @Override
    public void clearSubTasks() {
        prioritizedTasks.removeIf(task -> task instanceof SubTask);
        subTasks.values().forEach(this::releaseIntervals);
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.clearAssignedSubTasks();
            updateEpicStatus(epic);
        });
    }

    @Override
    public void clearEpics() {
        prioritizedTasks.removeIf(task -> task instanceof Epic || task instanceof SubTask);
        epics.values().forEach(this::releaseIntervals);
        subTasks.values().forEach(this::releaseIntervals);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        history.add(tasks.get(id));
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<SubTask> getSubTaskById(int id) {
        history.add(subTasks.get(id));
        return Optional.ofNullable(subTasks.get(id));
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        history.add(epics.get(id));
        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public void createNewTask(Task task) {
        if (hasOverlap(task)) {
            throw new IllegalArgumentException("Task time overlaps with an existing task or subtask");
        }
        task.setId(currentId);
        tasks.put(currentId, task);
        prioritizedTasks.add(task);
        reserveIntervals(task);
        currentId++;
    }

    @Override
    public void createNewSubTask(SubTask subTask) {
        if (hasOverlap(subTask)) {
            throw new IllegalArgumentException("Subtask overlaps with an existing task or subtask");
        }

        Epic epic = epics.get(subTask.getEpicId());
        subTask.setId(currentId);
        epic.addSubTask(subTask);
        updateEpicStatus(epic);

        subTasks.put(currentId, subTask);
        currentId++;
        prioritizedTasks.add(subTask);
        reserveIntervals(subTask);
    }

    @Override
    public void createNewEpic(Epic epic) {
        epic.setId(currentId);
        epics.put(currentId, epic);
        currentId++;
    }

    @Override
    public List<SubTask> getAssignedSubTasks(int id) {
        return epics.get(id).getAssignedSubTasks();
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        releaseIntervals(tasks.get(id));
        tasks.remove(id);
        history.remove(id);

    }

    @Override
    public void deleteSubtaskById(int id) {
        SubTask subTaskToDelete = subTasks.remove(id);
        Epic masterEpic = epics.get(subTaskToDelete.getEpicId());
        masterEpic.removeSubTaskFromEpic(subTaskToDelete);
        releaseIntervals(subTaskToDelete);
        updateEpicStatus(epics.get(subTaskToDelete.getEpicId()));
        history.remove(id);
        prioritizedTasks.remove(subTaskToDelete);
    }

    @Override
    public void deleteEpicById(int id) {
        prioritizedTasks.remove(epics.get(id));
        history.remove(id);
        getAssignedSubTasks(id).forEach(subtask -> {
            history.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
            releaseIntervals(subtask);
        });
        epics.remove(id);
        subTasks.keySet().removeIf(subTaskId -> subTasks.get(subTaskId).getEpicId() == id);
    }

    @Override
    public void updateTask(Task updatedTask) {
        int id = updatedTask.getId();
        releaseIntervals(tasks.get(id));

        if (hasOverlap(updatedTask)) {
            reserveIntervals(tasks.get(id));
            throw new IllegalArgumentException("Task time overlaps with an existing task or subtask");
        }
        prioritizedTasks.remove(tasks.get(id));
        tasks.put(id, updatedTask);
        prioritizedTasks.add(updatedTask);
        reserveIntervals(updatedTask);
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        int id = updatedSubTask.getId();
        Epic masterEpic = epics.get(updatedSubTask.getEpicId());
        SubTask oldSubTask = subTasks.get(id);

        releaseIntervals(oldSubTask);
        if (hasOverlap(updatedSubTask)) {
            reserveIntervals(oldSubTask);
            throw new IllegalArgumentException("Subtask time overlaps with an existing task or subtask");
        }
        masterEpic.removeSubTaskFromEpic(oldSubTask);
        masterEpic.addSubTask(updatedSubTask);
        reserveIntervals(updatedSubTask);
        updateEpicStatus(epics.get(updatedSubTask.getEpicId()));

        prioritizedTasks.remove(subTasks.get(id));
        subTasks.put(id, updatedSubTask);
        prioritizedTasks.add(updatedSubTask);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        int id = updatedEpic.getId();
        Epic oldEpic = epics.get(id);
        oldEpic.getAssignedSubTasks().forEach(updatedEpic::addSubTask);
        epics.put(id, updatedEpic);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Task::getStartTime))));
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    void updateEpicStatus(Epic epic) {
        List<SubTask> assignedSubTasks = epic.getAssignedSubTasks();

        if (assignedSubTasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        boolean allNew = assignedSubTasks.stream().allMatch(subTask -> subTask.getStatus() == Status.NEW);
        boolean allDone = assignedSubTasks.stream().allMatch(subTask -> subTask.getStatus() == Status.DONE);

        epic.setStatus(allDone ? Status.DONE : allNew ? Status.NEW : Status.IN_PROGRESS);

        LocalDateTime earliestTime = assignedSubTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = assignedSubTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime latestTime = assignedSubTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(earliestTime);
        epic.setDuration(duration);
        epic.setEndTime(latestTime);
    }

    private void initializeSchedule() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusYears(PLANNING_PERIOD_YEARS);

        while (start.isBefore(end)) {
            schedule.put(start, true);
            start = start.plusMinutes(INTERVAL_MINUTES);
        }
    }

    private boolean hasOverlap(Task newTask) {
        LocalDateTime start = newTask.getStartTime();
        LocalDateTime end = newTask.getEndTime();

        if (start == null || end == null) {
            return false;
        }

        while (start.isBefore(end)) {
            if (!schedule.getOrDefault(start, true)) {
                return true;
            }
            start = start.plusMinutes(INTERVAL_MINUTES);
        }
        return false;
    }

    private void reserveIntervals(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start == null || end == null) {
            return;
        }

        while (start.isBefore(end)) {
            schedule.put(start, false);
            start = start.plusMinutes(INTERVAL_MINUTES);
        }
    }

    private void releaseIntervals(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();

        if (start == null || end == null) {
            return;
        }

        while (start.isBefore(end)) {
            schedule.put(start, true);
            start = start.plusMinutes(INTERVAL_MINUTES);
        }
    }
}


