package ee.jaakobjaan.signing.request.api;

import ee.jaakobjaan.signing.audit.api.dto.AuditEventResponse;
import ee.jaakobjaan.signing.audit.service.AuditEventService;
import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.api.dto.SigningRequestResponse;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import ee.jaakobjaan.signing.request.service.SigningRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/signing-requests")
@Tag(name = "Signing Requests", description = "Signing request workflow operations")
public class SigningRequestController {
    private final SigningRequestService service;
    private final AuditEventService auditEventService;

    public SigningRequestController(SigningRequestService service, AuditEventService auditEventService) {
        this.service = service;
        this.auditEventService = auditEventService;
    }

    @Operation(summary = "Create a signing request")
    @PostMapping
    public SigningRequestResponse create(@Valid @RequestBody CreateSigningRequestRequest request) {
        return SigningRequestResponse.from(service.create(request));
    }

    @Operation(summary = "List signing requests")
    @GetMapping
    public List<SigningRequestResponse> findAll(@RequestParam(required=false) SigningRequestStatus status) {
        if (status != null) {
            return service.findByStatus(status).stream().map(SigningRequestResponse::from).toList();
        }
        return service.findAll().stream().map(SigningRequestResponse::from).toList();
    }

    @Operation(summary = "Get signing request by ID")
    @GetMapping("/{id}")
    public SigningRequestResponse findById(@PathVariable Long id) {
        return SigningRequestResponse.from(service.findById(id));
    }

    @Operation(summary = "Approve signing request")
    @PostMapping("/{id}/approve")
    public SigningRequestResponse approve(@PathVariable Long id) {
        return SigningRequestResponse.from(service.approve(id));
    }

    @Operation(summary = "Reject signing request")
    @PostMapping("/{id}/reject")
    public SigningRequestResponse reject(@PathVariable Long id) {
        return SigningRequestResponse.from(service.reject(id));
    }

    @Operation(summary = "Sign signing request")
    @PostMapping("/{id}/sign")
    public SigningRequestResponse sign(@PathVariable Long id) {
        return SigningRequestResponse.from(service.sign(id));
    }

    @Operation(summary = "Get audit history for signing request")
    @GetMapping("/{id}/audit-events")
    public List<AuditEventResponse> findAuditEvents(@PathVariable Long id) {
        service.findById(id);

        return auditEventService.findBySigningRequestId(id).stream()
                .map(AuditEventResponse::from)
                .toList();
    }
}
