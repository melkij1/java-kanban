package test.model;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class TaskTest {

    @Test
    public void taskEqualToEachOther() {
        Task task1 = new Task("Task1", "Description1");
        Task task2 = new Task("Task1", "Description1");
        Assertions.assertEquals(task1, task2);
    }

}