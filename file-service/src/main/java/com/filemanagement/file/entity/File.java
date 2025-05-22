package com.filemanagement.file.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "files", indexes = {
        @Index(name = "idx_file_name", columnList = "file_name")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "upload_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime uploadTime;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
