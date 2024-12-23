package com.yandex.add.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    @DisplayName("should be equal subtasks")
    public void shouldEqualWithSubtasks() {
        Epic epic = new Epic("epic", "epicDes");
        Subtask expected = new Subtask("1", "1", epic.getId());
        Subtask actual = new Subtask("1", "1", epic.getId());

        assertEqualSubtasks(expected, actual);
    }

    private static void assertEqualSubtasks(Subtask expected, Subtask actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getId(), actual.getId());
    }
}