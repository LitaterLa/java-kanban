package com.yandex.add.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    @DisplayName("epic must be equal to its copy")
    public void shouldEqualWithCopy(){
        Epic epic = new Epic("name", "description");
        Epic epic1 = new Epic("name", "description");
        assertEpicsEqual(epic, epic1, "must be equal");
    }

    private static void assertEpicsEqual(Task one, Task two, String message ){
        assertEquals(one.getIdNum(), two.getIdNum());
        assertEquals(one.getDescription(), two.getDescription());
        assertEquals(one.getTitle(), two.getTitle());

    }

}
