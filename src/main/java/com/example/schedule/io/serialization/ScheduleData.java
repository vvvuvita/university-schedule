package com.example.schedule.io.serialization;

import com.example.schedule.model.Lesson;
import java.io.Serializable;
import java.util.List;

public class ScheduleData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Lesson> lessons;

    public ScheduleData() {}

    public ScheduleData(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }
}
