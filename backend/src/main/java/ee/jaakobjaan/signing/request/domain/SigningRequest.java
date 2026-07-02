package ee.jaakobjaan.signing.request.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "signing_requests")
public class SigningRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "signer_email", nullable = false)
    private String signerEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SigningRequestStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SigningRequest(String title, String description, String signerEmail, LocalDateTime expiresAt) {
        this.title = title;
        this.description = description;
        this.signerEmail = signerEmail;
        this.expiresAt = expiresAt;
        this.status = SigningRequestStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected SigningRequest() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSignerEmail() {
        return signerEmail;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public SigningRequestStatus getStatus() {
        return status;
    }

    public void changeStatus(SigningRequestStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}
