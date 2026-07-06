package ee.jaakobjaan.signing.request.service;

import ee.jaakobjaan.signing.audit.domain.AuditEventType;
import ee.jaakobjaan.signing.audit.service.AuditEventService;
import ee.jaakobjaan.signing.common.exception.InvalidStatusTransitionException;
import ee.jaakobjaan.signing.common.exception.ResourceNotFoundException;
import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import ee.jaakobjaan.signing.request.repository.SigningRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SigningRequestService {

    private final SigningRequestRepository repository;
    private final AuditEventService auditEventService;

    public SigningRequestService(
            SigningRequestRepository repository,
            AuditEventService auditEventService
    ) {
        this.repository = repository;
        this.auditEventService = auditEventService;
    }

    @Transactional
    public SigningRequest create(CreateSigningRequestRequest request) {
        SigningRequest signingRequest = new SigningRequest(
                request.title(),
                request.description(),
                request.signerEmail(),
                request.expiresAt()
        );

        SigningRequest saved = repository.save(signingRequest);

        auditEventService.recordEvent(
                saved,
                AuditEventType.REQUEST_CREATED,
                null,
                saved.getStatus(),
                request.signerEmail(),
                "Signing request created."
        );

        return saved;
    }

    public List<SigningRequest> findAll() {
        return repository.findAll();
    }

    public List<SigningRequest> findByStatus(SigningRequestStatus status) {
        return repository.findByStatus(status);
    }

    public SigningRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signing request not found: " + id));
    }

    @Transactional
    public SigningRequest approve(Long id) {
        SigningRequest request = findById(id);

        if (request.getStatus() != SigningRequestStatus.CREATED) {
            throw new InvalidStatusTransitionException(
                    "Only CREATED requests can be approved. Current status: " + request.getStatus()
            );
        }

        SigningRequestStatus oldStatus = request.getStatus();
        request.changeStatus(SigningRequestStatus.PENDING_REVIEW);

        SigningRequest saved = repository.save(request);

        auditEventService.recordEvent(
                saved,
                AuditEventType.REQUEST_APPROVED,
                oldStatus,
                saved.getStatus(),
                "system",
                "Signing request approved."
        );

        return saved;
    }

    @Transactional
    public SigningRequest reject(Long id) {
        SigningRequest request = findById(id);

        if (request.getStatus() == SigningRequestStatus.SIGNED) {
            throw new InvalidStatusTransitionException("SIGNED request cannot be rejected.");
        }

        if (request.getStatus() == SigningRequestStatus.EXPIRED) {
            throw new InvalidStatusTransitionException("EXPIRED request cannot be rejected.");
        }

        SigningRequestStatus oldStatus = request.getStatus();
        request.changeStatus(SigningRequestStatus.REJECTED);

        SigningRequest saved = repository.save(request);

        auditEventService.recordEvent(
                saved,
                AuditEventType.REQUEST_REJECTED,
                oldStatus,
                saved.getStatus(),
                "system",
                "Signing request rejected."
        );

        return saved;
    }

    @Transactional
    public SigningRequest sign(Long id) {
        SigningRequest request = findById(id);

        if (request.getStatus() != SigningRequestStatus.PENDING_REVIEW) {
            throw new InvalidStatusTransitionException(
                    "Only PENDING_REVIEW requests can be signed. Current status: " + request.getStatus()
            );
        }

        SigningRequestStatus oldStatus = request.getStatus();
        request.changeStatus(SigningRequestStatus.SIGNED);

        SigningRequest saved = repository.save(request);

        auditEventService.recordEvent(
                saved,
                AuditEventType.REQUEST_SIGNED,
                oldStatus,
                saved.getStatus(),
                "system",
                "Signing request signed."
        );

        return saved;
    }
}