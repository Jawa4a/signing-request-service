package ee.jaakobjaan.signing.request.api;

import ee.jaakobjaan.signing.audit.api.dto.AuditEventResponse;
import ee.jaakobjaan.signing.request.api.dto.CreateSigningRequestRequest;
import ee.jaakobjaan.signing.request.api.dto.SigningRequestResponse;
import ee.jaakobjaan.signing.request.domain.SigningRequestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SigningRequestIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createSigningRequest_shouldPersistRequestAndCreateAuditEvent() {
        CreateSigningRequestRequest request = new CreateSigningRequestRequest(
                "Integration test request",
                "Testing full backend flow",
                "test@example.com",
                LocalDateTime.now().plusDays(7)
        );

        ResponseEntity<SigningRequestResponse> createResponse = restTemplate.postForEntity(
                "/api/signing-requests",
                request,
                SigningRequestResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();

        SigningRequestResponse created = createResponse.getBody();

        assertThat(created.id()).isNotNull();
        assertThat(created.title()).isEqualTo("Integration test request");
        assertThat(created.signerEmail()).isEqualTo("test@example.com");
        assertThat(created.status()).isEqualTo(SigningRequestStatus.CREATED);

        ResponseEntity<AuditEventResponse[]> auditResponse = restTemplate.getForEntity(
                "/api/signing-requests/" + created.id() + "/audit-events",
                AuditEventResponse[].class
        );

        assertThat(auditResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(auditResponse.getBody()).isNotNull();
        assertThat(auditResponse.getBody()).hasSize(1);
        assertThat(auditResponse.getBody()[0].eventType().name()).isEqualTo("REQUEST_CREATED");
        assertThat(auditResponse.getBody()[0].newStatus()).isEqualTo(SigningRequestStatus.CREATED);
    }

    @Test
    void approveAndSign_shouldCreateAuditHistory() {
        CreateSigningRequestRequest request = new CreateSigningRequestRequest(
                "Signing workflow test",
                "Testing approve and sign",
                "workflow@example.com",
                LocalDateTime.now().plusDays(7)
        );

        SigningRequestResponse created = restTemplate.postForObject(
                "/api/signing-requests",
                request,
                SigningRequestResponse.class
        );

        assertThat(created).isNotNull();

        ResponseEntity<SigningRequestResponse> approveResponse = restTemplate.postForEntity(
                "/api/signing-requests/" + created.id() + "/approve",
                null,
                SigningRequestResponse.class
        );

        assertThat(approveResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(approveResponse.getBody()).isNotNull();
        assertThat(approveResponse.getBody().status()).isEqualTo(SigningRequestStatus.PENDING_REVIEW);

        ResponseEntity<SigningRequestResponse> signResponse = restTemplate.postForEntity(
                "/api/signing-requests/" + created.id() + "/sign",
                null,
                SigningRequestResponse.class
        );

        assertThat(signResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(signResponse.getBody()).isNotNull();
        assertThat(signResponse.getBody().status()).isEqualTo(SigningRequestStatus.SIGNED);

        ResponseEntity<AuditEventResponse[]> auditResponse = restTemplate.getForEntity(
                "/api/signing-requests/" + created.id() + "/audit-events",
                AuditEventResponse[].class
        );

        assertThat(auditResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(auditResponse.getBody()).isNotNull();
        assertThat(auditResponse.getBody()).hasSize(3);

        assertThat(auditResponse.getBody()[0].eventType().name()).isEqualTo("REQUEST_CREATED");
        assertThat(auditResponse.getBody()[1].eventType().name()).isEqualTo("REQUEST_APPROVED");
        assertThat(auditResponse.getBody()[2].eventType().name()).isEqualTo("REQUEST_SIGNED");
    }

    @Test
    void rejectSignedRequest_shouldReturnConflict() {
        CreateSigningRequestRequest request = new CreateSigningRequestRequest(
                "Invalid transition test",
                "Testing conflict response",
                "conflict@example.com",
                LocalDateTime.now().plusDays(7)
        );

        SigningRequestResponse created = restTemplate.postForObject(
                "/api/signing-requests",
                request,
                SigningRequestResponse.class
        );

        assertThat(created).isNotNull();

        restTemplate.postForEntity(
                "/api/signing-requests/" + created.id() + "/approve",
                null,
                SigningRequestResponse.class
        );

        restTemplate.postForEntity(
                "/api/signing-requests/" + created.id() + "/sign",
                null,
                SigningRequestResponse.class
        );

        ResponseEntity<String> rejectResponse = restTemplate.postForEntity(
                "/api/signing-requests/" + created.id() + "/reject",
                null,
                String.class
        );

        assertThat(rejectResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(rejectResponse.getBody()).contains("SIGNED request cannot be rejected.");
    }
}