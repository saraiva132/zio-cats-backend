CREATE TABLE "user"
(
    "id"       INT PRIMARY KEY,
    "email"    VARCHAR NOT NULL,
    "username" VARCHAR NOT NULL,
    "password" VARCHAR NOT NULL,

    UNIQUE (email)
);