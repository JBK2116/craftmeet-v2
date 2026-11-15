CREATE UNIQUE INDEX uq_meetings_live_user_id
    ON meetings (user_id)
    WHERE start_time IS NOT NULL AND end_time IS NULL;
