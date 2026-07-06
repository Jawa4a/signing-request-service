package ee.jaakobjaan.signing.request.service;

import ee.jaakobjaan.signing.audit.domain.AuditEventType;
import ee.jaakobjaan.signing.audit.service.AuditEventService;
import ee.jaakobjaan.signing.common.exception.InvalidStatusTransitionException;
import ee.jaakobjaan.signing.common.exception.ResourceNotFoundException;
import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequest;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import ee.jaakobjaan.signing.request.repository.SigningRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SigningRequestServiceTest {

    @Mock
    private SigningRequestRepository repository;

    @Mock
    private AuditEventService auditEventService;

    @InjectMocks
    private SigningRequestService service;

    @Test
    void create_shouldCreateSigningRequestWithCreatedStatusAndAuditEvent() {
        CreateSigningRequestRequest request = new CreateSigningRequestRequest(
                "Contract",
                "Test contract",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );

        when(repository.save(any(SigningRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SigningRequest result = service.create(request);

        assertThat(result.getTitle()).isEqualTo("Contract");
        assertThat(result.getDescription()).isEqualTo("Test contract");
        assertThat(result.getSignerEmail()).isEqualTo("test@example.com");
        assertThat(result.getStatus()).isEqualTo(SigningRequestStatus.CREATED);

        verify(repository).save(any(SigningRequest.class));

        verify(auditEventService).recordEvent(
                eq(result),
                eq(AuditEventType.REQUEST_CREATED),
                isNull(),
                eq(SigningRequestStatus.CREATED),
                eq("test@example.com"),
                eq("Signing request created.")
        );
    }

    @Test
    void findById_shouldReturnSigningRequestWhenExists() {
        SigningRequest request = new SigningRequest(
                "Contract",
                "Test contract",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(request));

        SigningRequest result = service.findById(1L);

        assertThat(result).isSameAs(request);
    }

    @Test
    void findById_shouldThrowResourceNotFoundExceptionWhenMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Signing request not found: 999");
    }

    @Test
    void approve_shouldChangeCreatedToPendingReviewAndCreateAuditEvent() {
        SigningRequest request = new SigningRequest(
                "Contract",
                "Test contract",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(any(SigningRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SigningRequest result = service.approve(1L);

        assertThat(result.getStatus()).isEqualTo(SigningRequestStatus.PENDING_REVIEW);

        verify(auditEventService).recordEvent(
                eq(result),
                eq(AuditEventType.REQUEST_APPROVED),
                eq(SigningRequestStatus.CREATED),
                eq(SigningRequestStatus.PENDING_REVIEW),
                eq("system"),
                eq("Signing request approved.")
        );
    }

    @Test
    void sign_shouldChangePendingReviewToSignedAndCreateAuditEvent() {
        SigningRequest request = new SigningRequest(
                "Contract",
                "Test contract",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );
        request.changeStatus(SigningRequestStatus.PENDING_REVIEW);

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(repository.save(any(SigningRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SigningRequest result = service.sign(1L);

        assertThat(result.getStatus()).isEqualTo(SigningRequestStatus.SIGNED);

        verify(auditEventService).recordEvent(
                eq(result),
                eq(AuditEventType.REQUEST_SIGNED),
                eq(SigningRequestStatus.PENDING_REVIEW),
                eq(SigningRequestStatus.SIGNED),
                eq("system"),
                eq("Signing request signed.")
        );
    }

    @Test
    void sign_shouldThrowInvalidStatusTransitionExceptionWhenStatusIsCreated() {
        SigningRequest request = new SigningRequest(
                "Contract",
                "Test contract",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.sign(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessage("Only PENDING_REVIEW requests can be signed. Current status: CREATED");

        verify(repository, never()).save(any());
        verifyNoInteractions(auditEventService);
    }

    @Test
    void reject_shouldThrowInvalidStatusTransitionExceptionWhenStatusIsSigned() {
        SigningRequest request = new SigningRequest(
                "Contract",
                "Test contract",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );
        request.changeStatus(SigningRequestStatus.PENDING_REVIEW);
        request.changeStatus(SigningRequestStatus.SIGNED);

        when(repository.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.reject(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessage("SIGNED request cannot be rejected.");

        verify(repository, never()).save(any());
        verifyNoInteractions(auditEventService);
    }
}