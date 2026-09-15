CREATE TABLE clubs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description TEXT,
    date_of_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    CONSTRAINT fk_club_creator FOREIGN KEY (created_by) REFERENCES "user" (id) ON DELETE RESTRICT
);

