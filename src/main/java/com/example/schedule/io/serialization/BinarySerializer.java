package com.example.schedule.io.serialization;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class BinarySerializer implements Serializer<ScheduleData> {

    @Override
    public String format() { return "bin"; }

    @Override
    public void save(ScheduleData data, Path file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(data);
        }
    }

    @Override
    public ScheduleData load(Path file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return (ScheduleData) ois.readObject();
        }
    }
}
