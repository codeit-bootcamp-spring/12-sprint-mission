DROP TABLE IF EXISTS message_attachments;
DROP TABLE IF EXISTS read_statuses;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS channels;
DROP TABLE IF EXISTS binary_contents;

CREATE TABLE binary_contents
(
	id           uuid PRIMARY KEY,
	created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
	file_name    varchar(255)             NOT NULL,
	size         bigint                   NOT NULL,
	content_type varchar(100)             NOT NULL
);

CREATE TABLE channels
(
	id          uuid PRIMARY KEY,
	created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
	updated_at  TIMESTAMP WITH TIME ZONE NULL,
	name        varchar(100)             NULL,
	description varchar(500)             NULL,
	type        varchar(20)              NOT NULL
);

CREATE TABLE users
(
	id         uuid PRIMARY KEY,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL,
	updated_at TIMESTAMP WITH TIME ZONE NULL,
	username   varchar(50)              NOT NULL,
	email      varchar(100)             NOT NULL,
	password   varchar(60)              NOT NULL,
	profile_id uuid                     NULL,
	role       varchar(20)              NOT NULL,
	CONSTRAINT fk_users_profile_id FOREIGN KEY (profile_id) REFERENCES binary_contents (id) ON DELETE SET NULL,
	CONSTRAINT uk_users_username UNIQUE (username),
	CONSTRAINT uk_users_email UNIQUE (email),
	CONSTRAINT uk_users_profile_id UNIQUE (profile_id)
);

CREATE TABLE read_statuses
(
	id           uuid PRIMARY KEY,
	created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
	updated_at   TIMESTAMP WITH TIME ZONE NULL,
	user_id      uuid                     NOT NULL,
	channel_id   uuid                     NOT NULL,
	last_read_at TIMESTAMP WITH TIME ZONE NOT NULL,
	CONSTRAINT fk_read_statuses_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	CONSTRAINT fk_read_statuses_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
	CONSTRAINT uk_read_statuses_user_channel UNIQUE (user_id, channel_id)
);

CREATE TABLE messages
(
	id         uuid PRIMARY KEY,
	created_at TIMESTAMP WITH TIME ZONE NOT NULL,
	updated_at TIMESTAMP WITH TIME ZONE NULL,
	content    text                     NULL,
	channel_id uuid                     NOT NULL,
	author_id  uuid                     NULL,
	CONSTRAINT fk_messages_channel_id FOREIGN KEY (channel_id) REFERENCES channels (id) ON DELETE CASCADE,
	CONSTRAINT fk_messages_author_id FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE message_attachments
(
	message_id    uuid NOT NULL,
	attachment_id uuid NOT NULL,
	CONSTRAINT fk_attachments_message_id FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
	CONSTRAINT fk_attachments_attachment_id FOREIGN KEY (attachment_id) REFERENCES binary_contents (id) ON DELETE CASCADE
);
