CREATE TABLE notifications (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id   BINARY(16)   NOT NULL,
    message    TEXT         NOT NULL,
    go_to      VARCHAR(255) NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);
