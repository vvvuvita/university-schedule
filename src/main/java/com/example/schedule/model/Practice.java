package com.example.schedule.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Practice extends Lesson implements Editable {

    private String subgroup;
    public Practice() {
        super();
    }
    public Practice(LocalDateTime dateTime, String group, String discipline,
                    String room, String teacher, String subgroup) {
        super(dateTime, group, discipline, room, teacher);
        this.subgroup = subgroup;
    }
    public String getSubgroup() {
        return subgroup;
    }

    public void setSubgroup(String subgroup) {
        this.subgroup = subgroup;
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
        if (subgroup == null || subgroup.isBlank()) {
            errors.add("Error! The subgroup is not specified");
        }

        return errors;
    }
}
