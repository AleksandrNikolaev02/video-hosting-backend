package com.example.email_service.service;

import com.example.dto.EmailRequestDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class EmailService {
    private final JavaMailSender sender;

    @KafkaListener(groupId = "${kafka.group-id}",
                   topics = "${topics.email-request}",
                   containerFactory = "containerFactoryEmailRequestDTO")
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
}
