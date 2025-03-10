package com.dan.service.impl;

import com.cloudinary.Cloudinary;
import com.dan.model.FileUpload;
import com.dan.repository.FileUploadRepository;
import com.dan.service.FileUploadService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public FileUpload uploadFile(String fileName, MultipartFile multipartFile) throws IOException {
        // Generate a random file code
        String fileCode = RandomStringUtils.randomAlphanumeric(8);

        // Extract file extension
        String originalFileName = multipartFile.getOriginalFilename();
        String fileExtension = originalFileName != null ?
            originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase() : "";

        // Create public ID for Cloudinary
        String publicId = fileCode + "-" + fileName;

        // Determine resource type based on file extension
        String resourceType = "auto";
        if (fileExtension.equals(".mp4") || fileExtension.equals(".mov") ||
            fileExtension.equals(".avi") || fileExtension.equals(".wmv")) {
            resourceType = "video";
        }

        // Prepare upload parameters
        Map<String, Object> params = new HashMap<>();
        params.put("public_id", publicId);
        params.put("resource_type", resourceType);

        // For videos, you might want to add these parameters
        if (resourceType.equals("video")) {
            // Optional: Add video-specific parameters if needed
            // params.put("chunk_size", 6000000); // For larger files
            // params.put("eager", "..."); // For video transformations
        }

        // Upload to Cloudinary
        Map uploadResult;
        try {
            // For videos, consider using a different approach for larger files
            if (resourceType.equals("video") && multipartFile.getSize() > 10000000) { // >10MB
                // Create a temporary file
                File tempFile = File.createTempFile("video-upload", fileExtension);
                multipartFile.transferTo(tempFile);
                uploadResult = cloudinary.uploader().upload(tempFile, params);
                tempFile.delete(); // Clean up
            } else {
                uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), params);
            }
        } catch (Exception e) {
            throw new IOException("Error uploading to Cloudinary: " + e.getMessage(), e);
        }

        // Create and save file upload entity
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileType(fileExtension);
        fileUpload.setFileCode(fileCode);
        fileUpload.setSize(multipartFile.getSize());
        fileUpload.setUrl((String) uploadResult.get("secure_url"));
        fileUpload.setPublicId(publicId);

        return fileUploadRepository.save(fileUpload);
    }

    @Override
    public Resource getFileAsResource(String fileCode) throws IOException {
        // Find the file by code
        FileUpload fileUpload = fileUploadRepository.findByFileCode(fileCode)
            .orElseThrow(() -> new IOException("File not found with code: " + fileCode));

        // Return the URL as a resource
        return new UrlResource(fileUpload.getUrl());
    }

    @Override
    public void deleteFile(Long id) throws IOException {
        // Find the file
        FileUpload fileUpload = fileUploadRepository.findById(id)
            .orElseThrow(() -> new IOException("File not found with id: " + id));

        // Check if public ID exists before attempting to delete from Cloudinary
        if (fileUpload.getPublicId() != null && !fileUpload.getPublicId().isEmpty()) {
            try {
                // Delete from Cloudinary - make sure we're passing the public_id correctly
                Map<String, Object> params = new HashMap<>();
                params.put("resource_type", "auto");
                cloudinary.uploader().destroy(fileUpload.getPublicId(), params);
            } catch (Exception e) {
                // Log the error but continue with database deletion
                System.err.println("Error deleting from Cloudinary: " + e.getMessage());
            }
        }

        // Delete from database
        fileUploadRepository.deleteById(id);
    }
}