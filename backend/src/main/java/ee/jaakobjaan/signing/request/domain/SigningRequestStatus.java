package ee.jaakobjaan.signing.request.domain;

public enum SigningRequestStatus {
    CREATED,
    PENDING_REVIEW,
    SIGNED,
    REJECTED,
    EXPIRED,
    FAILED
}
