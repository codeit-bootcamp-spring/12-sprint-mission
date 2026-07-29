-- PostgreSQL 초기화 스크립트
-- docker-compose.yml에서 /docker-entrypoint-initdb.d/schema.sql 로 마운트되어
-- PostgreSQL 컨테이너가 최초 생성될 때 자동 실행됩니다.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS binary_contents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP(6) WITH TIME ZONE,
                                email VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(60) NOT NULL,
    role VARCHAR(20) NOT NULL,
    profile_id UUID,

    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_profile UNIQUE (profile_id),

    CONSTRAINT fk_users_profile
    FOREIGN KEY (profile_id)
    REFERENCES binary_contents(id)
    );

CREATE TABLE IF NOT EXISTS user_statuses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP(6) WITH TIME ZONE,
                                user_id UUID NOT NULL,
                                last_active_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

                                CONSTRAINT uk_user_statuses_user UNIQUE (user_id),

    CONSTRAINT fk_user_statuses_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
                            ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS channels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP(6) WITH TIME ZONE,
                                name VARCHAR(100),
    type VARCHAR(10) NOT NULL,
    description VARCHAR(500)
    );

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP(6) WITH TIME ZONE,
                                content TEXT,
                                channel_id UUID NOT NULL,
                                author_id UUID,

                                CONSTRAINT fk_messages_channel
                                FOREIGN KEY (channel_id)
    REFERENCES channels(id)
                            ON DELETE CASCADE,

    CONSTRAINT fk_messages_author
    FOREIGN KEY (author_id)
    REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS message_attachments (
                                                   message_id UUID NOT NULL,
                                                   attachment_id UUID NOT NULL,

                                                   CONSTRAINT pk_message_attachments
                                                   PRIMARY KEY (message_id, attachment_id),

    CONSTRAINT fk_message_attachments_message
    FOREIGN KEY (message_id)
    REFERENCES messages(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_message_attachments_binary_content
    FOREIGN KEY (attachment_id)
    REFERENCES binary_contents(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS read_statuses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP(6) WITH TIME ZONE,
                                user_id UUID NOT NULL,
                                channel_id UUID NOT NULL,
                                last_read_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,

                                CONSTRAINT uk_read_statuses_user_channel
                                UNIQUE (user_id, channel_id),

    CONSTRAINT fk_read_statuses_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
                            ON DELETE CASCADE,

    CONSTRAINT fk_read_statuses_channel
    FOREIGN KEY (channel_id)
    REFERENCES channels(id)
                            ON DELETE CASCADE
    );

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'USER';
