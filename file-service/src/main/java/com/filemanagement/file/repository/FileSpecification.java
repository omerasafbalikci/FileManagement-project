package com.filemanagement.file.repository;

import com.filemanagement.file.entity.File;
import com.filemanagement.file.utilities.exceptions.UnexpectedException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Specification class for filtering patients based on various criteria.
 *
 * @author Ömer Asaf BALIKÇI
 */

@RequiredArgsConstructor
public class FileSpecification implements Specification<File> {
    private final String fileName;
    private final String fileType;
    private final String filePath;
    private final String uploadTime;
    private final Boolean deleted;

    /**
     * Constructs a {@link Predicate} based on the filtering criteria provided.
     *
     * @param root            the root
     * @param query           the criteria query
     * @param criteriaBuilder the criteria builder
     * @return a {@link Predicate} representing the filtering conditions
     */
    @Override
    public Predicate toPredicate(@NonNull Root<File> root, CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (fileName != null && !fileName.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("fileName"), "%" + fileName + "%"));
        }
        if (fileType != null && !fileType.isEmpty()) {
            predicates.add(criteriaBuilder.equal(root.get("fileType"), fileType));
        }
        if (filePath != null && !filePath.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("filePath"), "%" + filePath + "%"));
        }
        if (uploadTime != null && !uploadTime.isEmpty()) {
            List<DateTimeFormatter> formatters = Arrays.asList(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            );
            boolean parsed = false;
            for (DateTimeFormatter formatter : formatters) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(uploadTime, formatter);
                    predicates.add(criteriaBuilder.equal(root.get("uploadTime"), dateTime));
                    parsed = true;
                    break;
                } catch (DateTimeParseException ignored) {
                }
            }
            if (!parsed) {
                throw new UnexpectedException("Invalid date format: " + uploadTime);
            }
        }
        if (deleted != null) {
            predicates.add(criteriaBuilder.equal(root.get("deleted"), deleted));
        } else {
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
