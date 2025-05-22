package com.filemanagement.file.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse implements Serializable {
    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
    private String uploadTime;
}
