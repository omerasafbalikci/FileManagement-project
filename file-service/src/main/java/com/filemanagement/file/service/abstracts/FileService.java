package com.filemanagement.file.service.abstracts;

import com.filemanagement.file.dto.requests.FileUploadRequest;
import com.filemanagement.file.dto.responses.FileResponse;
import com.filemanagement.file.dto.responses.PagedResponse;

public interface FileService {
    FileResponse getFileById(Long id);

    PagedResponse<FileResponse> getAllFilesFilteredAndSorted(int page, int size, String sortBy, String direction, String fileName,
                                                             String fileType, String filePath, String uploadTime, Boolean deleted);

    FileResponse uploadFile(FileUploadRequest fileUploadRequest);

    void deleteFile(Long id);
}
