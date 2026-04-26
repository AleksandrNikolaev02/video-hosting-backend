package com.example.camunda.util;

import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator implements IdGenerator {
    @Override
    public String generate() {
        return UuidCreator.getTimeOrderedEpoch().toString();
    }
}
