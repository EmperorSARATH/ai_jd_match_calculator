CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE candidates (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    resume TEXT NOT NULL,
    embedding vector(384)
);