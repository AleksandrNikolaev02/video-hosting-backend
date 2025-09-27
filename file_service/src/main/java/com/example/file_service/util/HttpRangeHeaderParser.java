package com.example.file_service.util;

public class HttpRangeHeaderParser {
    public static long getStart(String header) {
        return Long.parseLong(header.split("=")[1].split("-")[0]);
    }

    public static long getEnd(String header) {
        return Long.parseLong(header.split("=")[1].split("-")[1]);
    }
}
