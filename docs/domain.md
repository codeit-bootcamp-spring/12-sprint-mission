## 연관관계

- User : BinaryContent | 1:1 | User -> BinaryContent | 주인: User
- User : ReadStatus | 1:N | User <-> ReadStatus | 주인: ReadStatus
- Channel : ReadStatus | 1:N | Channel <-> ReadStatus | 주인: ReadStatus
- Channel : Message | 1:N | Message -> Channel | 주인: Message
- User : Message | 1:N | Message -> User | 주인: Message
- Message : BinaryContent | 1:N | Message -> BinaryContent | 주인: Message

## BaseEntity

- UUID id
- Instant createdAt

## BaseUpdatableEntity

- Instant updatedAt

## Domain Model

### User

- 상속: BaseUpdatableEntity
- 필드
    - String username (unique)
    - String email (unique)
    - String password
    - BinaryContent profile
    - List<ReadStatus> readStatuses
    - UserStatus status

### Channel

- 상속: BaseUpdatableEntity
- 필드
    - ChannelType type (PUBLIC / PRIVATE)
    - String name (PUBLIC일 때만 사용)
    - String description
    - List<ReadStatus> readStatuses

### Message

- 상속: BaseUpdatableEntity
- 필드
    - Channel channel
    - User author
    - String content
    - List<BinaryContent> attachments

### BinaryContent(immutable)

- 상속: BaseEntity
- 필드
    - String fileName
    - String contentType
    - Long size

### ReadStatus

- 상속: BaseUpdatableEntity
- 필드
    - User user
    - Channel channel
    - Instant lastReadAt
