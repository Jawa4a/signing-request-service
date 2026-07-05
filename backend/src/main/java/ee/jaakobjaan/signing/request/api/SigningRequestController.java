package ee.jaakobjaan.signing.request.api;

import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.api.dto.SigningRequestResponse;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import ee.jaakobjaan.signing.request.service.SigningRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signing-requests")
public class SigningRequestController {
    private final SigningRequestService service;

    public SigningRequestController(SigningRequestService service) {
        this.service = service;
    }

    @PostMapping
    public SigningRequestResponse create(@Valid @RequestBody CreateSigningRequestRequest request) {
        return SigningRequestResponse.from(service.create(request));
    }

    @GetMapping
    public List<SigningRequestResponse> findAll(@RequestParam(required=false) SigningRequestStatus status) {
        if (status != null) {
            return service.findByStatus(status).stream().map(SigningRequestResponse::from).toList();
        }
        return service.findAll().stream().map(SigningRequestResponse::from).toList();
    }

    @GetMapping("/{id}")
    public SigningRequestResponse findById(@PathVariable Long id) {
        return SigningRequestResponse.from(service.findById(id));
    }

    @PostMapping("/{id}/approve")
    public SigningRequestResponse approve(@PathVariable Long id) {
        return SigningRequestResponse.from(service.approve(id));
    }

    @PostMapping("/{id}/reject")
    public SigningRequestResponse reject(@PathVariable Long id) {
        return SigningRequestResponse.from(service.reject(id));
    }

    @PostMapping("/{id}/sign")
    public SigningRequestResponse sign(@PathVariable Long id) {
        return SigningRequestResponse.from(service.sign(id));
    }
}
