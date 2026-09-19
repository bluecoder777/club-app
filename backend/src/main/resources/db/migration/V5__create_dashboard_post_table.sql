CREATE TABLE dashboard_post (
    id BIGSERIAL PRIMARY KEY,
    club_id BIGINT NOT NULL,
    title VARCHAR NOT NULL,
    description TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    last_updated_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dashboard_club FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    CONSTRAINT fk_dashboard_created_by FOREIGN KEY (created_by) REFERENCES "user" (id) ON DELETE RESTRICT,
    CONSTRAINT fk_dashboard_updated_by FOREIGN KEY (last_updated_by) REFERENCES "user" (id) ON DELETE RESTRICT
);

