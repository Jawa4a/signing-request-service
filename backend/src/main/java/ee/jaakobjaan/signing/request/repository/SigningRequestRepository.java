package ee.jaakobjaan.signing.request.repository;

import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SigningRequestRepository extends JpaRepository<SigningRequest, Long> {
    List<SigningRequest> findByStatus(SigningRequestStatus status);

    List<SigningRequest> findBySignerEmail(String signerEmail);
}
