-- 1. 기존 테이블 안전하게 삭제 (외래키 무시하고 연쇄 삭제)
DROP TABLE IF EXISTS "message_attachments" CASCADE;
DROP TABLE IF EXISTS "read_statuses" CASCADE;
DROP TABLE IF EXISTS "user_statuses" CASCADE;
DROP TABLE IF EXISTS "messages" CASCADE;
DROP TABLE IF EXISTS "channels" CASCADE;
DROP TABLE IF EXISTS "users" CASCADE;
DROP TABLE IF EXISTS "binary_contents" CASCADE;


-- 2. 테이블 생성 (PK, UK, NN 제약조건 포함)

CREATE TABLE "binary_contents"
(
    "id"           UUID PRIMARY KEY,
    "created_at"   timestamptz  NOT NULL,
    "file_name"    varchar(255) NOT NULL,
    "size"         bigint       NOT NULL,
    "content_type" varchar(100) NOT NULL
--     "bytes"        bytea        NOT NULL
);

CREATE TABLE "users"
(
    "id"         UUID PRIMARY KEY,
    "created_at" timestamptz  NOT NULL,
    "updated_at" timestamptz  NULL,
    "username"   varchar(50)  NOT NULL UNIQUE, -- UK, NN
    "email"      varchar(100) NOT NULL UNIQUE, -- UK, NN
    "password"   varchar(60)  NOT NULL,
    "profile_id" UUID UNIQUE  NULL             -- UK, FK 지정 예정 (탈퇴 시 NULL 처리를 위해 NULL 허용)
);

CREATE TABLE "channels"
(
    "id"          UUID PRIMARY KEY,
    "created_at"  timestamptz  NOT NULL,
    "updated_at"  timestamptz  NULL,
    "name"        varchar(100) NULL,
    "description" varchar(500) NULL,
    "type"        varchar(10)  NOT NULL CHECK ("type" IN ('PUBLIC', 'PRIVATE')) -- ENUM 처리
);

CREATE TABLE "messages"
(
    "id"         UUID PRIMARY KEY,
    "created_at" timestamptz NOT NULL,
    "updated_at" timestamptz NULL,
    "content"    text        NULL,
    "channel_id" UUID        NOT NULL,
    "author_id"  UUID        NULL -- 탈퇴 시 SET NULL 처리를 위해 NULL 허용
);

CREATE TABLE "message_attachments"
(
    "message_id"    UUID NOT NULL,
    "attachment_id" UUID NOT NULL UNIQUE,
    PRIMARY KEY ("message_id", "attachment_id")
);

CREATE TABLE "user_statuses"
(
    "id"             UUID PRIMARY KEY,
    "created_at"     timestamptz NOT NULL,
    "updated_at"     timestamptz NULL,
    "user_id"        UUID        NOT NULL UNIQUE, -- 1:1 관계를 위한 UK
    "last_active_at" timestamptz NOT NULL
);

CREATE TABLE "read_statuses"
(
    "id"           UUID PRIMARY KEY,
    "created_at"   timestamptz NOT NULL,
    "updated_at"   timestamptz NULL,
    "user_id"      UUID        NOT NULL,
    "channel_id"   UUID        NOT NULL,
    "last_read_at" timestamptz NOT NULL,
    UNIQUE ("user_id", "channel_id") -- 동일한 방에 중복 읽음 상태 방지 (UK)
);


-- 3. 외래키(FK) 및 ON DELETE 관계 설정

-- users 테이블
ALTER TABLE "users"
    ADD CONSTRAINT "FK_users_profile_id"
        FOREIGN KEY ("profile_id") REFERENCES "binary_contents" ("id") ON DELETE SET NULL;

-- messages 테이블
ALTER TABLE "messages"
    ADD CONSTRAINT "FK_messages_channel_id"
        FOREIGN KEY ("channel_id") REFERENCES "channels" ("id") ON DELETE CASCADE;
ALTER TABLE "messages"
    ADD CONSTRAINT "FK_messages_author_id"
        FOREIGN KEY ("author_id") REFERENCES "users" ("id") ON DELETE SET NULL;

-- message_attachments 테이블
ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_ma_message_id"
        FOREIGN KEY ("message_id") REFERENCES "messages" ("id") ON DELETE CASCADE;
ALTER TABLE "message_attachments"
    ADD CONSTRAINT "FK_ma_attachment_id"
        FOREIGN KEY ("attachment_id") REFERENCES "binary_contents" ("id") ON DELETE CASCADE;

-- user_statuses 테이블
ALTER TABLE "user_statuses"
    ADD CONSTRAINT "FK_us_user_id"
        FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

-- read_statuses 테이블
ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_rs_user_id"
        FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;
ALTER TABLE "read_statuses"
    ADD CONSTRAINT "FK_rs_channel_id"
        FOREIGN KEY ("channel_id") REFERENCES "channels" ("id") ON DELETE CASCADE;