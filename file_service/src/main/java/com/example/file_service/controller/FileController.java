package com.example.file_service.controller;

import com.example.dto.VideoLoadDTO;
import com.example.file_service.dto.ChunkFileDTO;
import com.example.file_service.dto.DeletePreviewDTO;
import com.example.file_service.dto.FileEntityDTO;
import com.example.file_service.dto.GetPreviewDTO;
import com.example.file_service.dto.RequestGetPreviewDTO;
import com.example.file_service.dto.SaveChunkDTO;
import com.example.file_service.dto.SaveChunksDTO;
import com.example.file_service.dto.SavePreviewDTO;
import com.example.file_service.mapper.FileEntityMapper;
import com.example.file_service.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class FileController {
    private final FileService fileService;
    private final FileEntityMapper fileEntityMapper;

    @Operation(summary = "Сохранить кусок файла (тестовый endpoint, нужно будет переписать)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return content type of file!"),
            @ApiResponse(responseCode = "500", description = "Error saving file in Minio!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/save_chunk")
    public ResponseEntity<String> saveChunk(@Validated @RequestBody SaveChunkDTO dto,
                                            @RequestHeader("X-user-id") long userId,
                                            @RequestHeader("X-path-to-file") String pathFile) throws IOException {
        Path path = Path.of(pathFile);
        byte[] file = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);

        final int length = file.length;
        final int chunkSize = 5_242_880;
        int count = length / chunkSize + (length % chunkSize == 0 ? 0 : 1);

        for (int i = 0; i < count; ++i) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, length);

            byte[] chunk = Arrays.copyOfRange(file, start, end);

            VideoLoadDTO videoLoadDTO = new VideoLoadDTO(chunk, contentType, "part" + i, dto.getKey());
            fileService.storeChunkFile(videoLoadDTO, userId, i, dto.getFilename());
        }

        return ResponseEntity.ok(contentType);
    }

    @Operation(summary = "Сохранение всех кусков файла в один файл")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File was compare and save!")
    })
    @PostMapping(value = "/save_all")
    public ResponseEntity<String> compareAndSaveChunks(@Validated @RequestBody SaveChunksDTO dto) {
        fileService.saveChunkFile(dto);

        return ResponseEntity.ok("File was compare and save!");
    }

    @Operation(summary = "Сгенерировать уникальный ключ для файла")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Long.class)))
    })
    @GetMapping(value = "/unique_key")
    public ResponseEntity<Long> getUniqueKey() {
        return ResponseEntity.ok().body(fileService.findUniqueKeyForFile());
    }

    @Operation(summary = "Получить все файлы пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(
                         mediaType = "application/json",
                         array = @ArraySchema(schema = @Schema(implementation = FileEntityDTO.class))
            ))
    })
    @GetMapping(value = "/files")
    public ResponseEntity<List<FileEntityDTO>> getFilesByUser(@RequestParam(value = "user_id") Long userId) {
        return ResponseEntity.ok(fileService.getFileEntitiesByUserId(userId)
                .stream()
                .map(fileEntityMapper::getFileEntityDTOFromFileEntity)
                .collect(Collectors.toList()));
    }

    @Operation(summary = "Сохранить превью в облаке Minio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The file was saved successfully!"),
            @ApiResponse(responseCode = "500", description = "Error saving file in Minio!",
                         content = @Content(mediaType = "application/json",
                         schema = @Schema(implementation = String.class)))
    })
    @PostMapping(value = "/save_preview")
    public ResponseEntity<String> savePreview(@Validated @RequestBody SavePreviewDTO dto,
                                              @RequestHeader("X-user-id") Long userId) {
        fileService.savePreview(dto, userId);

        return ResponseEntity.ok("The file was saved successfully!");
    }

    @Operation(summary = "Получение превью видео")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(mediaType = "image/jpeg", schema = @Schema(
                         implementation = RequestGetPreviewDTO.class))),
            @ApiResponse(responseCode = "404", description = "File not found by filename: <filename>")
    })
    @PostMapping(value = "/get_preview")
    public ResponseEntity<GetPreviewDTO> getPreview(@Validated @RequestBody RequestGetPreviewDTO dto) {
        return ResponseEntity.ok(fileService.getPreview(dto));
    }

    @Operation(summary = "Удаление превью видео")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "File not found by filename: <filename>")
    })
    @DeleteMapping(value = "delete_preview")
    public ResponseEntity<Void> deletePreview(@Validated @RequestBody DeletePreviewDTO dto) {
        fileService.deletePreview(dto);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Потоковый стриминг видео контента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Файл не найден в MinIO по пути"),
            @ApiResponse(responseCode = "206", description = "Байтовый массив",
                         content = @Content(
                                 schema = @Schema(type = "string", format = "binary")
                         ))
    })
    @GetMapping(value = "/file_chunk")
    public ResponseEntity<byte[]> getFileChunk(@RequestParam("user_id") Long userId,
                                               @RequestParam("filename") String filename,
                                               @RequestHeader(value = "Range", required = false) String rangeHeader) {
        ChunkFileDTO chunk = fileService.getChunkFile(filename, userId, rangeHeader);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.valueOf(chunk.contentType()))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", chunk.start(),
                        chunk.end(), chunk.fileLength()))
                .contentLength(chunk.end() - chunk.start() + 1)
                .body(chunk.data());
    }
}
