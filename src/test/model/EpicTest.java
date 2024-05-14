package test.model;

import model.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class EpicTest {
    @Test
    public void epicEqualToEachOther() {
        Epic epic1 = new Epic("Epic1", "Description1");
        Epic epic2 = new Epic("Epic1", "Description1");
        Assertions.assertEquals(epic1, epic2);
    }
}