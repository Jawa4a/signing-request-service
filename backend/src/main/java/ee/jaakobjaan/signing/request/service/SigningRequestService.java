package ee.jaakobjaan.signing.request.service;

import ee.jaakobjaan.signing.common.exception.InvalidStatusTransitionException;
import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import ee.jaakobjaan.signing.request.repository.SigningRequestRepository;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
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

    public SigningRequest findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResolutionException("Signing request not found: " + id));
    }

    public SigningRequest approve(Long id) {
        SigningRequest request = findById(id);

        if (request.getStatus() != SigningRequestStatus.CREATED) {
            throw new InvalidStatusTransitionException("Only CREATED requests can be approved. Current status: " + request.getStatus());
        }

        request.changeStatus(SigningRequestStatus.PENDING_REVIEW);
        return repository.save(request);
    }

    public SigningRequest reject(Long id) {
        SigningRequest request = findById(id);

        if (request.getStatus() == SigningRequestStatus.SIGNED) {
            throw new InvalidStatusTransitionException("SIGNED request cannot be rejected.");
        }

        if (request.getStatus() == SigningRequestStatus.EXPIRED) {
            throw new InvalidStatusTransitionException("EXPIRED request cannot be rejected.");
        }

        request.changeStatus(SigningRequestStatus.REJECTED);
        return repository.save(request);
    }

    public SigningRequest sign(Long id) {
        SigningRequest request = findById(id);

        if (request.getStatus() != SigningRequestStatus.PENDING_REVIEW) {
            throw new InvalidStatusTransitionException("Only PENDING_REVIEW requests can be signed. Current status: " + request.getStatus());
        }

        request.changeStatus(SigningRequestStatus.SIGNED);
        return repository.save(request);
    }
}
