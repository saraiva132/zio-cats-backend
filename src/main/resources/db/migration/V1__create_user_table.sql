CREATE TABLE "users"
(
    "id"         INT PRIMARY KEY,
    "email"      VARCHAR NOT NULL,
    "first_name" VARCHAR NOT NULL,
    "last_name"  VARCHAR NOT NULL,

    UNIQUE (email)
);