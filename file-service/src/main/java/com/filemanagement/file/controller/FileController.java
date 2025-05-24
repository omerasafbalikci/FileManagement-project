package com.filemanagement.file.controller;

import com.filemanagement.file.dto.requests.FileUploadRequest;
import com.filemanagement.file.dto.responses.FileResponse;
import com.filemanagement.file.dto.responses.PagedResponse;
import com.filemanagement.file.service.abstracts.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Log4j2
public class FileController {
    private final FileService fileService;

    @GetMapping("/id/{id}")
    public ResponseEntity<FileResponse> getFileById(@PathVariable("id") Long id) {
        log.trace("Entering getFileById method in FileController class");
        FileResponse response = this.fileService.getFileById(id);
        log.info("Successfully fetched file with id: {}", id);
        log.trace("Exiting getFileById method in FileController class");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtered-and-sorted")
    public ResponseEntity<PagedResponse<FileResponse>> getAllFilesFilteredAndSorted(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String filePath,
            @RequestParam(required = false) String uploadTime,
            @RequestParam(required = false) Boolean deleted
    ) {
        log.trace("Entering getAllFilesFilteredAndSorted method in FileController class");
        log.info("Received request for filtered and sorted files with parameters: page={}, size={}, sortBy={}, direction={}, fileName={}, fileType={}, filePath={}, uploadTime={}, deleted={}",
                page, size, sortBy, direction, fileName, fileType, filePath, uploadTime, deleted);
        PagedResponse<FileResponse> response = this.fileService.getAllFilesFilteredAndSorted(page, size, sortBy, direction, fileName, fileType, filePath, uploadTime, deleted);
        log.info("Successfully fetched filtered and sorted files.");
        log.trace("Exiting getAllFilesFilteredAndSorted method in FileController class");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FileResponse> uploadFile(@RequestBody FileUploadRequest fileUploadRequest) {
        log.trace("Entering uploadFile method in FileController class");
        FileResponse response = this.fileService.uploadFile(fileUploadRequest);
        log.info("Successfully saved file: {}", response);
        log.trace("Exiting uploadFile method in FileController class");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable("id") Long id) {
        log.trace("Entering deleteFile method in FileController class");
        this.fileService.deleteFile(id);
        log.info("Successfully deleted file with id: {}", id);
        log.trace("Exiting deleteFile method in FileController class");
        return ResponseEntity.ok("File has been successfully deleted.");
    }
}
