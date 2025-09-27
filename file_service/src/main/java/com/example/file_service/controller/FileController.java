package com.example.file_service.controller;

import com.example.dto.FileDataDTO;
import com.example.dto.VideoLoadDTO;
import com.example.file_service.dto.ChunkFileDTO;
import com.example.file_service.dto.FileEntityDTO;
import com.example.file_service.dto.SaveChunksDTO;
import com.example.file_service.mapper.FileEntityMapper;
import com.example.file_service.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping(value = "/file")
    public ResponseEntity<FileDataDTO> getFile(@RequestParam(value = "path") String path) {
        return ResponseEntity.ok(fileService.getFile(path));
    }

    @PostMapping(value = "/save_chunk")
    public ResponseEntity<String> saveChunk(@RequestBody Long key,
                                            @RequestHeader("X-user-id") long userId) throws IOException {
        Path path = Path.of("C:\\Users\\aunik\\OneDrive\\Desktop\\diplom\\backend\\file_service\\src\\main\\resources\\test_file.mp4");
        byte[] file = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);

        int length = file.length;
        final int chunkSize = 5_242_880;
        int count = length / chunkSize + (length % chunkSize == 0 ? 0 : 1);

        for (int i = 0; i < count; ++i) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, length);

            byte[] chunk = Arrays.copyOfRange(file, start, end);

            VideoLoadDTO dto = new VideoLoadDTO(chunk, contentType, "part" + i, key);
            fileService.storeChunkFile(dto, userId, i);
        }

        return ResponseEntity.ok(contentType);
    }

    @PostMapping(value = "/save_all")
    public ResponseEntity<String> compareAndSaveChunks(@Validated @RequestBody SaveChunksDTO dto) {
        fileService.saveChunkFile(dto);

        return ResponseEntity.ok("File was compare and save!");
    }

    @GetMapping(value = "/unique_key")
    public ResponseEntity<Long> getUniqueKey() {
        return ResponseEntity.ok().body(fileService.findUniqueKeyForFile());
    }

    @GetMapping(value = "/files")
    public ResponseEntity<List<FileEntityDTO>> getFilesByUser(@RequestParam(value = "user_id") Long userId) {
        return ResponseEntity.ok(fileService.getFileEntitiesByUserId(userId)
                .stream()
                .map(fileEntityMapper::getFileEntityDTOFromFileEntity)
                .collect(Collectors.toList()));
    }

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
