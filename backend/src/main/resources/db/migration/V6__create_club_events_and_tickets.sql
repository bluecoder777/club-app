CREATE TABLE club_event (
    id BIGSERIAL PRIMARY KEY,
    club_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    venue VARCHAR(200) NOT NULL,
    event_time TIMESTAMP NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_club FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_created_by FOREIGN KEY (created_by) REFERENCES "user" (id) ON DELETE RESTRICT
);

CREATE TABLE event_ticket (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    booked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_event FOREIGN KEY (event_id) REFERENCES club_event (id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT uq_event_member_ticket UNIQUE (event_id, member_id)
);

CREATE INDEX idx_club_event_club_time ON club_event (club_id, event_time);
CREATE INDEX idx_event_ticket_event ON event_ticket (event_id);
