package com.dan.repository;

import com.dan.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
	Optional<FileUpload> findByFileCode(String fileCode);

}
