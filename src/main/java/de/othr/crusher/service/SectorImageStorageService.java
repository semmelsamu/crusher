package de.othr.crusher.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handles storing and deleting sector images on the local filesystem.
 */
@Service
public class SectorImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
        Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final Path sectorUploadRoot;
    private final DataSize maxFileSize;

    /**
     * Creates a storage service for sector images. Ensures the upload root exists and
     * reads size limits from configuration.
     *
     * @param sectorUploadDir filesystem path for sector uploads (relative or absolute)
     * @param maxFileSize maximum allowed file size (Spring DataSize format, e.g. "5MB")
     */
    public SectorImageStorageService(
        @Value("${app.uploads.sectors-dir:uploads/sectors}") String sectorUploadDir,
        @Value("${app.uploads.max-size:5MB}") String maxFileSize) {
        this.sectorUploadRoot = Paths.get(sectorUploadDir).toAbsolutePath().normalize();
        this.maxFileSize = DataSize.parse(maxFileSize);

        try {
            Files.createDirectories(this.sectorUploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize sector upload directory", e);
        }
    }

    /**
     * Stores the provided image for the sector and returns the public URL path.
     *
     * @param sectorId identifier of the sector the image belongs to
     * @param file image to store
     * @return public path that can be used in templates (e.g., /uploads/sectors/{id}/{file})
     */
    public String store(long sectorId, MultipartFile file) {
        validateFile(file);

        Path sectorDir = sectorUploadRoot.resolve(String.valueOf(sectorId));
        try {
            Files.createDirectories(sectorDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create sector upload directory", e);
        }

        String extension = resolveExtension(file);
        String filename = UUID.randomUUID().toString();
        if (!extension.isBlank()) {
            filename = filename + "." + extension;
        }

        Path destination = sectorDir.resolve(filename).normalize();
        if (!destination.startsWith(sectorUploadRoot)) {
            throw new IllegalStateException("Invalid destination path for upload");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store sector image", e);
        }

        return "/uploads/sectors/" + sectorId + "/" + destination.getFileName();
    }

    /**
     * Deletes an image if it was stored via this service.
     *
     * @param imagePath public image path stored on the entity
     */
    public void deleteIfStored(String imagePath) {
        resolveStoredPath(imagePath)
        .ifPresent(
        path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete sector image", e);
            }
        });
    }

    /**
     * Validates size and type of an uploaded image.
     *
     * @param file multipart file to validate
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No image provided");
        }

        if (file.getSize() > maxFileSize.toBytes()) {
            throw new IllegalArgumentException("Image exceeds maximum allowed size");
        }

        String contentType = file.getContentType();
        boolean allowedContentType = contentType != null
                                     && ALLOWED_CONTENT_TYPES.stream().anyMatch(ct -> ct.equalsIgnoreCase(contentType));
        boolean allowedExtension = !resolveExtension(file).isBlank();

        if (!allowedContentType && !allowedExtension) {
            throw new IllegalArgumentException("Unsupported image type");
        }
    }

    private String resolveExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(original);
        if (extension != null) {
            String lower = extension.toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(lower)) {
                return lower;
            }
        }

        String contentType = file.getContentType();
        if (contentType != null) {
            switch (contentType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            case "image/webp":
                return "webp";
            case "image/gif":
                return "gif";
            default:
                break;
            }
        }

        return "";
    }

    /**
     * Resolves a stored image path on disk, ensuring it is within the configured uploads
     * directory to prevent path traversal.
     *
     * @param imagePath public image path stored on the entity
     * @return optional absolute path when it belongs to sector uploads
     */
    private Optional<Path> resolveStoredPath(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return Optional.empty();
        }

        String normalized = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
        Path candidate = Paths.get(normalized);

        if (candidate.getNameCount() < 3) {
            return Optional.empty();
        }

        Path uploadsPrefix = Paths.get("uploads").resolve("sectors");
        if (!candidate.startsWith(uploadsPrefix)) {
            return Optional.empty();
        }

        Path relative = candidate.subpath(2, candidate.getNameCount());
        Path resolved = sectorUploadRoot.resolve(relative).normalize();

        if (!resolved.startsWith(sectorUploadRoot)) {
            return Optional.empty();
        }

        return Optional.of(resolved);
    }
}
