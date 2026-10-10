package com.example.schedule.io;

public class CsvException extends Exception {

    public enum Code {
        WRONG_FIELD_COUNT, //в строке не хватает полей
        BAD_NUMBER, //число не парсится
        BAD_DATE, //неверный формат даты-времени
        UNKNOWN_TYPE, //неизвестный тип занятия
        EMPTY_LINE //на всякий случай пустая строка
    }

    private final Code code;
    private final int lineNumber;

    public CsvException(Code code, int lineNumber, String message) {
        super("Line " + lineNumber + " [" + code + "]: " + message);
        this.code = code;
        this.lineNumber = lineNumber;
    }

    public Code getCode() {
        return code;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
