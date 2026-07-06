package ee.jaakobjaan.signing.audit.api.dto;

import ee.jaakobjaan.signing.audit.domain.AuditEvent;
import ee.jaakobjaan.signing.audit.domain.AuditEventType;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;

import java.time.LocalDateTime;

public record AuditEventResponse(
        Long id,
        Long signingRequestId,
        AuditEventType eventType,
        SigningRequestStatus oldStatus,
        SigningRequestStatus newStatus,
        String actorEmail,
        String message,
        LocalDateTime createdAt
) {
    public static AuditEventResponse from(AuditEvent event) {
        return new AuditEventResponse(
                event.getId(),
                event.getSigningRequest().getId(),
                event.getEventType(),
                event.getOldStatus(),
                event.getNewStatus(),
                event.getActorEmail(),
                event.getMessage(),
                event.getCreatedAt()
        );
    }
}
