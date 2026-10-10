package com.example.schedule.model;

import java.time.LocalDateTime;
public class CancelledLesson extends Lesson {
    private final String reason;
    private final LocalDateTime cancelledAt;
    public CancelledLesson(Lesson source, String reason, LocalDateTime cancelledAt) {
        super(source.getDateTime(), source.getGroup(), source.getDiscipline(),
                source.getRoom(), source.getTeacher());
        this.reason = reason;
        this.cancelledAt = cancelledAt;
    }
    public String getReason() {
        return reason;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    @Override
    public void setDateTime(LocalDateTime dateTime) {
        throw new UnsupportedOperationException("CancelledLesson is read-only");
    }

    @Override
    public void setGroup(String group) {
        throw new UnsupportedOperationException("CancelledLesson is read-only");
    }

    @Override
    public void setDiscipline(String discipline) {
        throw new UnsupportedOperationException("CancelledLesson is read-only");
    }

    @Override
    public void setRoom(String room) {
        throw new UnsupportedOperationException("CancelledLesson is read-only");
    }

    @Override
    public void setTeacher(String teacher) {
        throw new UnsupportedOperationException("CancelledLesson is read-only");
    }
        @Override
       public String toString() {
            return "[Cancelled] " + super.toString()
                    + " reason: " + reason
                    + " cancelled at: " + cancelledAt;
        }
}
