CREATE TYPE member_role AS ENUM ('MEMBER', 'ADMIN');

CREATE TABLE member (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    club_id BIGINT NOT NULL,
    role member_role NOT NULL,
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_member_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    CONSTRAINT fk_member_club FOREIGN KEY (club_id) REFERENCES clubs (id) ON DELETE CASCADE,
    CONSTRAINT uq_user_club UNIQUE (user_id, club_id)
);
