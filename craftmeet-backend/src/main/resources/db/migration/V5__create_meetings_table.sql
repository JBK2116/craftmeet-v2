CREATE TABLE meetings (
    id               UUID          NOT NULL,
    user_id          UUID          NOT NULL,
    title            VARCHAR(150)  NOT NULL,
    description      VARCHAR(2000),
    room_code        VARCHAR(8)    NOT NULL,
    duration         INTEGER       NOT NULL,
    participant_cap  INTEGER       NOT NULL,
    start_time       TIMESTAMPTZ,
    end_time         TIMESTAMPTZ,
    pdf_url          VARCHAR(2048),
    last_exported_at TIMESTAMPTZ,
    created_at       TIMESTAMPTZ   NOT NULL,
    updated_at       TIMESTAMPTZ   NOT NULL,
    CONSTRAINT pk_meetings PRIMARY KEY (id),
    CONSTRAINT uq_meetings_room_code UNIQUE (room_code),
    CONSTRAINT fk_meetings_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_meeting_duration CHECK (duration BETWEEN 1 AND 240),
    CONSTRAINT ck_meeting_participant_cap CHECK (participant_cap BETWEEN 1 AND 250)
);

CREATE UNIQUE INDEX uq_meetings_live_user_id
    ON meetings (user_id)
    WHERE start_time IS NOT NULL AND end_time IS NULL;
