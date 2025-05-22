package com.filemanagement.file.repository;

import com.filemanagement.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File> {
    Optional<File> findByIdAndDeletedFalse(Long id);
}
