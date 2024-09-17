package com.yandex.add.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    @DisplayName("Subtasks should be equal")
    public void shouldBeEqualTasks(){
        Task task = new Task("name", "description");
        Task task1 = new Task("name", "description");
        assertEqualTasks(task, task1);
    }

    @Test
    public void shouldBeEqualTasksInheritors(){
        Task epic1 = new Epic("epic1", "epic1");
        Task epic2 = new Epic ("epic1", "epic1");
        assertEqualTasks(epic1, epic2);
    }

    private void assertEqualTasks(Task expected, Task actual){
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getIdNum(), actual.getIdNum());
    }

}