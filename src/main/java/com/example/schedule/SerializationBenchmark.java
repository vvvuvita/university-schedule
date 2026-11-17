package com.example.schedule;

import com.example.schedule.io.serialization.BinarySerializer;
import com.example.schedule.io.serialization.JsonSerializer;
import com.example.schedule.io.serialization.ScheduleData;
import com.example.schedule.io.serialization.Serializer;
import com.example.schedule.model.Lesson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SerializationBenchmark {
    public static void main(String[] args) throws Exception {
        int recordsCount = 10_000;
        int runs = 5; // Среднее по 5 прогонам

        //генерим
        List<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < recordsCount; i++) {
            LocalDateTime time = LocalDateTime.of(2026, 10, 1, 8, 0).plusHours(i);
            lessons.add(new Lesson(time, "Group" + (i % 5), "Discipline", "Room", "Teacher"));
        }
        ScheduleData data = new ScheduleData(lessons);

        Serializer<ScheduleData> jsonSer = new JsonSerializer();
        Serializer<ScheduleData> binSer = new BinarySerializer();
        Path jsonFile = Path.of("test_data_10k.json");
        Path binFile = Path.of("test_data_10k.bin");

        //замеры сохранения
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) jsonSer.save(data, jsonFile);
        long jsonSaveTime = (System.nanoTime() - start) / 1_000_000 / runs; // среднее в мс

        start = System.nanoTime();
        for (int i = 0; i < runs; i++) binSer.save(data, binFile);
        long binSaveTime = (System.nanoTime() - start) / 1_000_000 / runs;

        //замеры згрузки
        start = System.nanoTime();
        for (int i = 0; i < runs; i++) jsonSer.load(jsonFile);
        long jsonLoadTime = (System.nanoTime() - start) / 1_000_000 / runs;

        start = System.nanoTime();
        for (int i = 0; i < runs; i++) binSer.load(binFile);
        long binLoadTime = (System.nanoTime() - start) / 1_000_000 / runs;

        //размер файлов
        long jsonSize = Files.size(jsonFile);
        long binSize = Files.size(binFile);

        System.out.println("FORMAT COMPARISON (10,000 records)");
        System.out.printf("JSON size: %d KB (%d bytes)%n", jsonSize / 1024, jsonSize);
        System.out.printf("BIN size: %d KB (%d bytes)%n", binSize / 1024, binSize);
        System.out.printf("Saving:%n", runs);
        System.out.printf(" JSON: %d ms%n", jsonSaveTime);
        System.out.printf("  BIN:  %d ms%n", binSaveTime);
        System.out.printf("Load:%n", runs);
        System.out.printf("  JSON: %d ms%n", jsonLoadTime);
        System.out.printf("  BIN:  %d ms%n", binLoadTime);
    }
}