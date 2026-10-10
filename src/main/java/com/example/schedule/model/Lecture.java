package com.example.schedule.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Lecture extends Lesson implements Editable {

    private String stream;

    public Lecture() {
        super();
    }

    public Lecture(LocalDateTime dateTime, String group, String discipline,
                   String room, String teacher, String stream) {
        super(dateTime, group, discipline, room, teacher);
        this.stream = stream;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (dateTime == null) {
            errors.add("Error! The date and time are not specified");
        }
        if (group == null || group.isBlank()) {
            errors.add("Error! The group is not specified");
        }
        if (discipline == null || discipline.isBlank()) {
            errors.add("Error! The discipline is not specified");
        }
        if (room == null || room.isBlank()) {
            errors.add("Error! The room is not specified");
        }
        if (teacher == null || teacher.isBlank()) {
            errors.add("Error! The teacher is not specified");
        }
        if (stream == null || stream.isBlank()) {
            errors.add("Error! The stream is not specified");
        }
        return errors;
    }

    @Override
    public String toString() {
        return "[Lecture] " + super.toString() + " stream: " + stream;
    }

}