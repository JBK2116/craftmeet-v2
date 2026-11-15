CREATE TABLE stats (
    id                       UUID          NOT NULL,
    meeting_id               UUID          NOT NULL,
    total_participants       INTEGER       NOT NULL DEFAULT 0,
    total_questions_asked    INTEGER       NOT NULL DEFAULT 0,
    total_responses_received INTEGER       NOT NULL DEFAULT 0,
    duration_seconds         INTEGER,
    created_at               TIMESTAMPTZ   NOT NULL,
    updated_at               TIMESTAMPTZ   NOT NULL,
    CONSTRAINT pk_stats PRIMARY KEY (id),
    CONSTRAINT uq_stats_meeting_id UNIQUE (meeting_id),
    CONSTRAINT fk_stats_meeting FOREIGN KEY (meeting_id)
        REFERENCES meetings (id) ON DELETE CASCADE
);
