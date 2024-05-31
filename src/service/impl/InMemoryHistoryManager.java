package service.impl;

import model.Task;
import service.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
//    /*private final Map<Integer, Task> history = new HashMap<>();*/

    final private LinkedListCustom history = new LinkedListCustom();


    @Override
    public void addHistory(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(id);
    }


    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }


    private static class LinkedListCustom {

        private static class Node<T> {
            private T task;
            private Node<T> next;
            private Node<T> prev;

            public Node(Node<T> prev, T task, Node<T> next) {
                this.prev = prev;
                this.task = task;
                this.next = next;
            }
        }

        final private Map<Integer, Node<Task>> historyMap = new HashMap<>();

        public void linkLast(Task task){
            if(historyMap.containsKey(task.getId())) {
                historyMap.remove(task.getId());
            }

            historyMap.put(task.getId(), new Node<>(null, task, null));

        }

        public void removeNode(int id){
            historyMap.remove(id);
        }


        public List<Task> getTasks() {
            List<Task> newListTasks = new ArrayList<>();
            for(Node<Task> historyNode : historyMap.values()){
                newListTasks.add(historyNode.task);
            }

            return newListTasks;
        }
    }

}
