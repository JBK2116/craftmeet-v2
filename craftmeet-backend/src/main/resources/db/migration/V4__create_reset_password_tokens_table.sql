CREATE TABLE reset_password_tokens (
    id         UUID         NOT NULL,
    user_id    UUID         NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_reset_password_tokens PRIMARY KEY (id),
    CONSTRAINT uq_reset_password_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT fk_reset_password_tokens_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE
);
