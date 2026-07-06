package ee.jaakobjaan.signing.audit.service;

import ee.jaakobjaan.signing.audit.domain.AuditEvent;
import ee.jaakobjaan.signing.audit.domain.AuditEventType;
import ee.jaakobjaan.signing.audit.repository.AuditEventRepository;
import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditEventService {

    private final AuditEventRepository repository;

    public AuditEventService(AuditEventRepository repository) {
        this.repository = repository;
    }

    public void recordEvent(
            SigningRequest signingRequest,
            AuditEventType eventType,
            SigningRequestStatus oldStatus,
            SigningRequestStatus newStatus,
            String actorEmail,
            String message
    ) {
        AuditEvent event = new AuditEvent(
                signingRequest,
                eventType,
                oldStatus,
                newStatus,
                actorEmail,
                message
        );

        repository.save(event);
    }

    public List<AuditEvent> findBySigningRequestId(Long signingRequestId) {
        return repository.findBySigningRequestIdOrderByCreatedAtAsc(signingRequestId);
    }
}
