package com.example.schedule.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CancelledLessonTest {

    private CancelledLesson createSample() {
        Lesson source = new Lesson(
                LocalDateTime.of(2026, 9, 27, 9, 0),
                "AVT-553", "History", "7-110", "Sidorov S.S."
        );
        return new CancelledLesson(
                source, "Teacher is sick",
                LocalDateTime.of(2026, 9, 25, 18, 0)
        );
    }
//конструктор копирует поля из Lesson и добавляет reason, cancelledAt
    @Test
    @DisplayName("add reason, cancelledAt")
    void constructor_copiesFieldsFromLesson() {
        CancelledLesson cancelled = createSample();

        assertEquals("AVT-553", cancelled.getGroup());
        assertEquals("History", cancelled.getDiscipline());
        assertEquals("7-110", cancelled.getRoom());
        assertEquals("Sidorov S.S.", cancelled.getTeacher());
        assertEquals("Teacher is sick", cancelled.getReason());
        assertEquals(LocalDateTime.of(2026, 9, 25, 18, 0), cancelled.getCancelledAt());
    }
//нет изменениям
    @Test
    @DisplayName("setGroup throws UnsupportedOperationException")
    void setGroup_throwsUnsupportedOperationException() {
        CancelledLesson cancelled = createSample();

        assertThrows(UnsupportedOperationException.class,
                () -> cancelled.setGroup("HACK"));
    }

    @Test
    @DisplayName("setDateTime throws UnsupportedOperationException")
    void setDateTime_throwsUnsupportedOperationException() {
        CancelledLesson cancelled = createSample();

        assertThrows(UnsupportedOperationException.class,
                () -> cancelled.setDateTime(LocalDateTime.now()));
    }

    @Test
    @DisplayName("CancelledLesson don't released Editable")
    void cancelledLesson_isNotEditable() {
        CancelledLesson cancelled = createSample();

        assertFalse(cancelled instanceof Editable);
    }

    @Test
    @DisplayName("toString contains a label [Cancelled] и reason")
    void toString_containsCancelledAndReason() {
        CancelledLesson cancelled = createSample();

        String s = cancelled.toString();
        assertTrue(s.contains("[Cancelled]"));
        assertTrue(s.contains("Teacher is sick"));
    }
}
