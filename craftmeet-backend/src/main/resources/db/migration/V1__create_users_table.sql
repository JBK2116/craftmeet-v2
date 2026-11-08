CREATE TABLE users (
    id          UUID         NOT NULL,
    google_id   VARCHAR(255),
    username    VARCHAR(30),
    email       VARCHAR(254) NOT NULL,
    password    VARCHAR(255),
    verified    BOOLEAN      NOT NULL,
    verified_at TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  NOT NULL,
    updated_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_google_id UNIQUE (google_id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email)
);
