package com.example.schedule.io.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;

public class JsonSerializer implements Serializer<ScheduleData> {

    private final ObjectMapper mapper;

    public JsonSerializer() {
        mapper = new ObjectMapper();
        // Регистрируем модуль для поддержки LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        // Красивый вывод
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public String format() { return "json"; }

    @Override
    public void save(ScheduleData data, Path file) throws IOException {
        mapper.writeValue(file.toFile(), data);
    }

    @Override
    public ScheduleData load(Path file) throws IOException {
        return mapper.readValue(file.toFile(), ScheduleData.class);
    }
}