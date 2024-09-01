# java-kanban
Repository for homework project.

проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
проверьте, что объект Subtask нельзя сделать своим же эпиком;

Не писал тесты для условий выше, т.к при попытке скомпилировать например:
Epic epic = new Epic(parameters);
epic.addSubTask(epic);
java сразу кидает incompatible types.

offtop to delete:
'Походу надо было в тестировщики идти :D'