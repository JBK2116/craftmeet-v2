CREATE TABLE participants (
    id          UUID          NOT NULL,
    meeting_id  UUID          NOT NULL,
    name        VARCHAR(50)   NOT NULL,
    created_at  TIMESTAMPTZ   NOT NULL,
    updated_at  TIMESTAMPTZ   NOT NULL,
    CONSTRAINT pk_participants PRIMARY KEY (id),
    CONSTRAINT fk_participants_meeting FOREIGN KEY (meeting_id)
        REFERENCES meetings (id) ON DELETE CASCADE
);

CREATE INDEX idx_participants_meeting_id ON participants (meeting_id);
