package ee.jaakobjaan.signing.audit.domain;

import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "signing_request_id", nullable = false)
    private SigningRequest signingRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private AuditEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private SigningRequestStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private SigningRequestStatus newStatus;

    @Column(name = "actor_email")
    private String actorEmail;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AuditEvent() {
    }

    public AuditEvent(
            SigningRequest signingRequest,
            AuditEventType eventType,
            SigningRequestStatus oldStatus,
            SigningRequestStatus newStatus,
            String actorEmail,
            String message
    ) {
        this.signingRequest = signingRequest;
        this.eventType = eventType;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.actorEmail = actorEmail;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public SigningRequest getSigningRequest() {
        return signingRequest;
    }

    public AuditEventType getEventType() {
        return eventType;
    }

    public SigningRequestStatus getOldStatus() {
        return oldStatus;
    }

    public SigningRequestStatus getNewStatus() {
        return newStatus;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
