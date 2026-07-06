CREATE TABLE audit_events (
                              id BIGSERIAL PRIMARY KEY,
                              signing_request_id BIGINT NOT NULL,
                              event_type VARCHAR(100) NOT NULL,
                              old_status VARCHAR(50),
                              new_status VARCHAR(50),
                              actor_email VARCHAR(255),
                              message TEXT,
                              created_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_audit_events_signing_request
                                  FOREIGN KEY (signing_request_id)
                                      REFERENCES signing_requests(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_audit_events_signing_request_id
    ON audit_events(signing_request_id);

CREATE INDEX idx_audit_events_event_type
    ON audit_events(event_type);

CREATE INDEX idx_audit_events_created_at
    ON audit_events(created_at);