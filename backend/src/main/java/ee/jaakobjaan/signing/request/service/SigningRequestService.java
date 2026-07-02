package ee.jaakobjaan.signing.request.service;

import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import ee.jaakobjaan.signing.request.repository.SigningRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SigningRequestService {
    private final SigningRequestRepository repository;


    public SigningRequestService(SigningRequestRepository repository) {
        this.repository = repository;
    }

    public SigningRequest create(CreateSigningRequestRequest request) {
        SigningRequest signingRequest = new SigningRequest(
                request.title(),
                request.description(),
                request.signerEmail(),
                request.expiresAt()
        );
        return repository.save(signingRequest);
    }

    public List<SigningRequest> findAll() {
        return repository.findAll();
    }

    public List<SigningRequest> findByStatus(SigningRequestStatus status) {
        return repository.findByStatus(status);
    }
}
