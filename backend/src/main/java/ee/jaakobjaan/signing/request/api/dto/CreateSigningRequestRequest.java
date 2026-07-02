package ee.jaakobjaan.signing.request.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateSigningRequestRequest (
    @NotBlank
    String title,
    String description,
    @NotBlank
    @Email
    String signerEmail,
    @NotNull
    @Future
    LocalDateTime expiresAt
) {}
