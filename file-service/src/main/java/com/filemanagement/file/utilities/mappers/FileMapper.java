package com.filemanagement.file.utilities.mappers;

import com.filemanagement.file.dto.responses.FileResponse;
import com.filemanagement.file.entity.File;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {
    public FileResponse toGetFileResponse(File file) {
        if (file == null) {
            return null;
        }
        FileResponse fileResponse = new FileResponse();
        fileResponse.setId(file.getId());
        fileResponse.setFileName(file.getFileName());
        fileResponse.setFileType(file.getFileType());
        fileResponse.setFilePath(file.getFilePath());
        fileResponse.setUploadTime(file.getUploadTime().toString());
        return fileResponse;
    }
}
