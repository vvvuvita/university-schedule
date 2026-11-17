package com.example.schedule.serialization;

import com.example.schedule.io.serialization.BinarySerializer;
import com.example.schedule.io.serialization.JsonSerializer;
import com.example.schedule.io.serialization.ScheduleData;
import com.example.schedule.io.serialization.Serializer;
import com.example.schedule.model.Lesson;
import com.example.schedule.model.Lecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SerializerTest {

    private ScheduleData testData;
    private Serializer<ScheduleData> jsonSerializer;
    private Serializer<ScheduleData> binarySerializer;

    @BeforeEach
    void setUp() {
        jsonSerializer = new JsonSerializer();
        binarySerializer = new BinarySerializer();

        List<Lesson> lessons = new ArrayList<>();
        lessons.add(new Lesson(LocalDateTime.of(2026, 10, 1, 10, 0), "AVT-552", "Java", "101", "Ivanov"));
        lessons.add(new Lecture(LocalDateTime.of(2026, 10, 1, 12, 0), "AVT-552", "Math", "102", "Petrov", "Stream-1"));

        testData = new ScheduleData(lessons);
    }

//позитивные сценарии
    @Test
    @DisplayName("JSON: Сохранение и загрузка должны вернуть равные данные")
    void jsonSerializer_roundtrip_isSuccessful(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.json");

        jsonSerializer.save(testData, file);
        ScheduleData loaded = jsonSerializer.load(file);

        assertEquals(2, loaded.getLessons().size());
        assertEquals("AVT-552", loaded.getLessons().get(0).getGroup());
        assertEquals("Stream-1", ((Lecture) loaded.getLessons().get(1)).getStream());
    }

    @Test
    @DisplayName("BIN: Сохранение и загрузка должны вернуть равные данные")
    void binarySerializer_roundtrip_isSuccessful(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("test.bin");

        binarySerializer.save(testData, file);
        ScheduleData loaded = binarySerializer.load(file);

        assertEquals(2, loaded.getLessons().size());
        assertEquals("Java", loaded.getLessons().get(0).getDiscipline());
    }

    @Test
    @DisplayName("Оба: Сохранение пустого списка не вызывает ошибок")
    void serializers_saveEmptyData_worksFine(@TempDir Path tempDir) throws Exception {
        ScheduleData emptyData = new ScheduleData(new ArrayList<>());
        Path jsonFile = tempDir.resolve("empty.json");
        Path binFile = tempDir.resolve("empty.bin");

        jsonSerializer.save(emptyData, jsonFile);
        binarySerializer.save(emptyData, binFile);

        assertTrue(Files.exists(jsonFile));
        assertTrue(Files.exists(binFile));
        assertEquals(0, jsonSerializer.load(jsonFile).getLessons().size());
    }

//негативные сценарии

    @Test
    @DisplayName("JSON: Попытка загрузить битый JSON-файл выбрасывает IOException")
    void jsonSerializer_loadBrokenFile_throwsException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("broken.json");
        Files.writeString(file, "не JSON!");

        //исключение при чтении битого файла
        assertThrows(IOException.class, () -> jsonSerializer.load(file));
    }

    @Test
    @DisplayName("BIN: Попытка загрузить битый бинарный файл выбрасывает IOException")
    void binarySerializer_loadBrokenFile_throwsException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("broken.bin");
        Files.writeString(file, "Сломанный машинный код");
        assertThrows(IOException.class, () -> binarySerializer.load(file));
    }

    @Test
    @DisplayName("Оба: Загрузка несуществующего файла выбрасывает IOException")
    void serializers_loadNonExistentFile_throwsException(@TempDir Path tempDir) {
        Path nonExistent = tempDir.resolve("no_such_file.json");
        assertThrows(IOException.class, () -> jsonSerializer.load(nonExistent));
        assertThrows(IOException.class, () -> binarySerializer.load(nonExistent));
    }

    @Test
    @DisplayName("BIN: Нарушение версии (serialVersionUID) ломает чтение")
    void binarySerializer_invalidVersion_throwsException(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("wrong_version.bin");
        binarySerializer.save(testData, file);
        byte[] bytes = Files.readAllBytes(file);
        bytes[4] = (byte) 99;
        Files.write(file, bytes);
        assertThrows(Exception.class, () -> binarySerializer.load(file));
    }
}