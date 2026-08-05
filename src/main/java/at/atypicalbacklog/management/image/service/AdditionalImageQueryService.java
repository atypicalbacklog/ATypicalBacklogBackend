package at.atypicalbacklog.management.image.service;

import at.atypicalbacklog.management.image.domain.AdditionalImage;
import at.atypicalbacklog.management.image.dto.AdditionalImageMetadataDto;
import at.atypicalbacklog.management.image.dto.PageResponseDto;
import at.atypicalbacklog.management.image.repository.AdditionalImageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AdditionalImageQueryService {

    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 200;

    private final AdditionalImageRepository repository;

    public AdditionalImageQueryService(AdditionalImageRepository repository) {
        this.repository = repository;
    }

    public PageResponseDto<AdditionalImageMetadataDto> list(int page, int size) {
        int safePage = Math.max(0, page);
        int safeSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        Page<AdditionalImage> result = repository.findAll(
            PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "creationDate"))
        );

        return new PageResponseDto<>(
            result.getContent().stream().map(this::toDto).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    public AdditionalImageMetadataDto getMetadata(String uid) {
        return repository.findById(uid)
            .map(this::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Additional image not found"));
    }

    private AdditionalImageMetadataDto toDto(AdditionalImage img) {
        return new AdditionalImageMetadataDto(
            img.getUid(),
            img.getCreationDate(),
            img.getType(),
            img.getAspectHint(),
            img.getSource(),
            img.getFilename(),
            img.getMimetype()
        );
    }
}
