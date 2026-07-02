CREATE TABLE signing_requests (
                                  id BIGSERIAL PRIMARY KEY,
                                  title VARCHAR(255) NOT NULL,
                                  description TEXT,
                                  signer_email VARCHAR(255) NOT NULL,
                                  status VARCHAR(50) NOT NULL,
                                  expires_at TIMESTAMP NOT NULL,
                                  created_at TIMESTAMP NOT NULL,
                                  updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_signing_requests_status ON signing_requests(status);
CREATE INDEX idx_signing_requests_signer_email ON signing_requests(signer_email);
CREATE INDEX idx_signing_requests_created_at ON signing_requests(created_at);