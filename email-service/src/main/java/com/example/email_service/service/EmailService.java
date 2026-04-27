package com.example.email_service.service;

import com.example.dto.EmailRequestDTO;
import com.example.email_service.config.AppConfig;
import com.example.email_service.model.TwoFactorCode;
import com.example.email_service.repository.TwoFactorCodeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender sender;
    private final AppConfig config;
    private final TwoFactorCodeRepository factorCodeRepository;
    private final Random random = new Random();

    // Нужно для оповещений.
    // Сделать как-нибудь потом
//    @KafkaListener(groupId = "${kafka.group-id}",
//                   topics = "${topics.email-request}",
//                   containerFactory = "containerFactoryEmailRequestDTO")
    public void handleRequest(EmailRequestDTO dto) {
        log.info("Получен запрос на отправку письма!");

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(dto.getEmail());
        message.setSubject("Уведомление о новом видео!");
        message.setText(String.format("Недавно было опубликовано новое видео %s на канале %s",
                dto.getVideoName(), dto.getChannelName()));

        log.info("Отправляю письмо по адресу: {}", dto.getEmail());

        sender.send(message);

        log.info("Окончание обработки запроса на отправку письма...");
    }

    @KafkaListener(groupId = "${kafka.group-id}",
            topics = "${topics.email-request}",
            containerFactory = "kafkaListenerContainerFactory",
            errorHandler = "sendRequestEmailErrorHandler")
    public void handleCodeRequest(String email) {
        log.info("Получен запрос на генерацию двухфакторного кода и отправка его на почту {}", email);

        Integer code = random.nextInt(config.getMinCode(), config.getMaxCode());

        TwoFactorCode twoFactorCode = new TwoFactorCode();
        twoFactorCode.setEmail(email);
        twoFactorCode.setCreatedAt(LocalDateTime.now());
        twoFactorCode.setCode(code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Письмо от MyTube");
        message.setText("Ваш код: %s".formatted(code));

        factorCodeRepository.save(twoFactorCode);

        sender.send(message);

        log.info("Письмо отправлено на почту {}", email);
    }
}
