package com.example.file_service.util;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.uuid.UuidValueGenerator;

import java.util.UUID;

@Slf4j
public class UuidV7Generator implements UuidValueGenerator {
    @Override
    public UUID generateUuid(SharedSessionContractImplementor sharedSessionContractImplementor) {
        UUID uuid = UuidCreator.getTimeOrderedEpoch();

        log.info("New uuid: {}", uuid);

        return uuid;
    }
}
