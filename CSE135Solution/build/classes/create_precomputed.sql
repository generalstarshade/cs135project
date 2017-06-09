create table precomputed (
    id SERIAL PRIMARY KEY,
    state_id INTEGER REFERENCES state (id) NOT NULL,
    state_name TEXT NOT NULL,