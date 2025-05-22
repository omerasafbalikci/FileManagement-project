package com.filemanagement.file.service.concretes;

import com.filemanagement.file.dto.requests.FileUploadRequest;
import com.filemanagement.file.dto.responses.FileResponse;
import com.filemanagement.file.dto.responses.PagedResponse;
import com.filemanagement.file.entity.File;
import com.filemanagement.file.repository.FileRepository;
import com.filemanagement.file.repository.FileSpecification;
import com.filemanagement.file.service.abstracts.FileService;
import com.filemanagement.file.utilities.exceptions.FileNotFoundException;
import com.filemanagement.file.utilities.exceptions.UnexpectedException;
import com.filemanagement.file.utilities.mappers.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Cacheable(value = "files", key = "#id", unless = "#result == null")
    public FileResponse getFileById(Long id) {
        log.trace("Entering getFileById method in FileServiceImpl");
        log.info("Fetching file by ID: {}", id);
        File file = this.fileRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> {
            log.error("File not found with id: {}", id);
            return new FileNotFoundException("File not found with id: " + id);
        });
        FileResponse response = this.fileMapper.toGetFileResponse(file);
        log.info("Successfully fetched file by ID: {}", id);
        log.trace("Exiting getFileById method in FileServiceImpl");
        return response;
    }

    @Override
    public PagedResponse<FileResponse> getAllFilesFilteredAndSorted(int page, int size, String sortBy, String direction, String fileName,
                                                                    String fileType, String filePath, String uploadTime, Boolean deleted) {
        log.trace("Entering getAllFilesFilteredAndSorted method in FileServiceImpl");
        log.debug("Fetching all files with filters: page={}, size={}, sortBy={}, direction={}, fileName={}, fileType={}, filePath={}, uploadTime={}, deleted={}",
                page, size, sortBy, direction, fileName, fileType, filePath, uploadTime, deleted);
        Pageable pagingSort = PageRequest.of(page, size, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        FileSpecification specification = new FileSpecification(fileName, fileType, filePath, uploadTime, deleted);
        Page<File> filePage = this.fileRepository.findAll(specification, pagingSort);
        List<FileResponse> fileResponses = filePage.getContent()
                .stream()
                .map(this.fileMapper::toGetFileResponse)
                .toList();
        log.info("Successfully fetched files with filters: page={}, size={}, totalPages={}, totalElements={}", page, size, filePage.getTotalPages(), filePage.getTotalElements());
        log.trace("Exiting getAllFilesFilteredAndSorted method in FileServiceImpl");
        return new PagedResponse<>(
                fileResponses,
                filePage.getNumber(),
                filePage.getTotalPages(),
                filePage.getTotalElements(),
                filePage.getSize(),
                filePage.isFirst(),
                filePage.isLast(),
                filePage.hasNext(),
                filePage.hasPrevious()
        );
    }

    @Override
    @CachePut(value = "files", key = "#result.id", unless = "#result == null")
    public FileResponse uploadFile(FileUploadRequest fileUploadRequest) {
        log.trace("Entering uploadFile method in FileServiceImpl");
        try {
            String fileName = System.currentTimeMillis() + "_" + fileUploadRequest.getFile().getOriginalFilename();
            Path targetPath = Paths.get(uploadDir + java.io.File.separator + fileName);
            Files.createDirectories(targetPath.getParent());
            Files.copy(fileUploadRequest.getFile().getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            File file = File.builder()
                    .fileName(fileName)
                    .fileType(fileUploadRequest.getFile().getContentType())
                    .filePath(targetPath.toString())
                    .uploadTime(LocalDateTime.now())
                    .build();

            this.fileRepository.save(file);

            FileResponse fileResponse = this.fileMapper.toGetFileResponse(file);
            log.trace("Exiting uploadFile method in FileServiceImpl");
            return fileResponse;
        } catch (IOException exception) {
            throw new UnexpectedException("" + exception);
        }
    }

    @Override
    @CacheEvict(value = "files", key = "#id")
    public void deleteFile(Long id) {
        log.trace("Entering deleteFile method in FileServiceImpl");
        log.info("Deleting file with ID: {}", id);
        File file = this.fileRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> {
            log.error("File not found to delete. ID: {}", id);
            return new FileNotFoundException("File not found to delete. ID: " + id);
        });

        try {
            Files.deleteIfExists(Paths.get(file.getFilePath()));
        } catch (IOException exception) {
            throw new UnexpectedException("" + exception);
        }

        file.setDeleted(true);
        this.fileRepository.save(file);
        log.info("Successfully deleted file with ID: {}", id);
        log.trace("Exiting deleteFile method in FileServiceImpl");
    }
}
