package ru.yandex.practicum.TaskTracker;

import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Epic extends Task {
    private static HashMap<Integer, Collection<SubTask>> assignedSubTasks = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
        setStatus(Status.NEW);
    }

    public static void addSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Collection<SubTask> subTaskList = assignedSubTasks.getOrDefault(epicId, new ArrayList<>());
        subTaskList.add(subTask);
        assignedSubTasks.put(epicId, subTaskList);
    }

    public static Collection<SubTask> getAssignedSubTasks(Epic epic) {
        int id = epic.getId();
        return assignedSubTasks.getOrDefault(id, new ArrayList<>());
    }

    public static void removeAssignedTasks(Epic epic) {
        int id = epic.getId();
        assignedSubTasks.remove(id);
    }

    public static void removeSubTaskFromEpic(SubTask subTask) {
        int epicId = subTask.getEpicId();
        Collection<SubTask> subTaskList = assignedSubTasks.get(epicId);
        if (subTaskList != null) {
            subTaskList.remove(subTask);
            if (subTaskList.isEmpty()) {
                assignedSubTasks.remove(epicId);
            } else {
                assignedSubTasks.put(epicId, subTaskList);
            }
        }
    }

    public static void clearAssignedSubTasks() {
        assignedSubTasks.clear();
    }

    public static void updateEpicStatus(Epic epic) {
        if (assignedSubTasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        Collection<SubTask> checkStatus = getAssignedSubTasks(epic);
        for (SubTask subTask : checkStatus) {
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}

