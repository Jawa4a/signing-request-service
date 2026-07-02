package ee.jaakobjaan.signing.request.api.dto;

import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;

import java.time.LocalDateTime;

public record SigningRequestResponse (
    Long id,
    String title,
    String description,
    String signerEmail,
    SigningRequestStatus status,
    LocalDateTime expiresAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static SigningRequestResponse from(SigningRequest request) {
        return new SigningRequestResponse(
                request.getId(),
                request.getTitle(),
                request.getDescription(),
                request.getSignerEmail(),
                request.getStatus(),
                request.getExpiresAt(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
