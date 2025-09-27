package com.example.file_service.interfaces;

public interface Mapper {
    String serialize(Object objectToSerialize);
    <T> T deserialize(String value, Class<T> clazz);
}
