package com.example.email_service.service;

import com.example.dto.CheckEmailDTO;
import com.example.dto.Status;
import com.example.dto.TwoFactorCodeDTO;
import com.example.email_service.config.AppConfig;
import com.example.email_service.model.TwoFactorCode;
import com.example.email_service.repository.TwoFactorCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TwoFactorCodeService {
    private TwoFactorCodeRepository codeRepository;
    private AppConfig config;
    private TransactionService transactionService;

    public CheckEmailDTO verify(TwoFactorCodeDTO dto) {
        List<TwoFactorCode> codes = codeRepository.findAllTwoFactorCodeByEmail(dto.getLogin());

        CheckEmailDTO response = new CheckEmailDTO();
        response.setEmail(dto.getLogin());

        for (TwoFactorCode code : codes) {
            if (code.getCode().equals(dto.getCode()) && !isExpiredCode(code)) {
                response.setStatus(Status.OK);
                return response;
            }
        }

        for (TwoFactorCode code : codes) {
            if (code.getCode().equals(dto.getCode())) {
                response.setStatus(Status.UNAUTHORIZED);
                return response;
            }
        }

        response.setStatus(Status.NOT_FOUND);

        return response;
    }

    @Async
    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedDelayString = "${app.time-delay-clear-code}")
    public void clearExpiredCoded() {
        transactionService.requireNewTransaction(
                () -> codeRepository.deleteBatch(LocalDateTime.now().minusMinutes(config.getTimeToLiveCode()), config.getLimitToDelete())
        );
    }

    private boolean isExpiredCode(TwoFactorCode code) {
        return code.getCreatedAt().plusMinutes(config.getTimeToLiveCode()).isBefore(LocalDateTime.now());
    }

    @Autowired
    public void setCodeRepository(TwoFactorCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Autowired
    public void setConfig(AppConfig config) {
        this.config = config;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
}
