create table "users" (
  "id" UUID PRIMARY KEY NOT NULL,
  "email" VARCHAR(1024) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NULL
);
