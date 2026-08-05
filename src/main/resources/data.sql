INSERT INTO users (
    id,
    created_at,
    updated_at,
    username,
    email,
    password,
    profile_id,
    role
)
VALUES (
           '550e8400-e29b-41d4-a716-446655440000',
           NOW(),
           NOW(),
           'test',
           'testuser@example.com',
           '$2a$10$4mMSq6FMkpLB05SjkCh5zOoXCHH1STyWRey.P6jldFdJ5aztUyrLa',
           NULL,
            'USER'
       );