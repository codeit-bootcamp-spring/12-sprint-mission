<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 3</summary>

### Sprint 3

- Sprint 2에서 스프링 프로젝트로 변경.
- docs를 현재 프로젝트 기준으로 최신화.
- 멘토님의 첨삭을 참고하여 기존 코드 수정 및 리펙토링 진행.
- 엔티티
    - 복사 생성자는 그대로 유지하고 copyOf로 생성 의도 명확히 전달.
    - 객체를 직접 참조하지 않고 UUID를 참조해서 약한 결합으로 수정.
    - 엔티티의 필요없는 필드 및 메소드 제거.(단순화)
    - 생성, 수정시간을 Instant로 수정.
    - getter 어노테이션 추가.
    - 도메인 추가(ReadStatus, UserStatus, BinaryContent)
    - DTO추가에 따른 엔티티의 toString 부분 제거.

- DTO
    - 기존 서비스와 컨트롤러 사이의 매개체를 엔티티에서 DTO로 변경
    - 요구사항에 따른 createDTO, updateDTO, responseDTO등을 생성

- 레포지토리
    - find의 반환값을 optional로 변경.(null 처리 안정성 확보)
    - 단일 조회 메서드를 선언형으로 변경.
    - @Repository과 @ConditionalOnProperty 사용.(File* 계열만)
    - @ConditionalOnProperty을 통해 사용하는 repo 타입 지정.

- 서비스
    - 다른 객체를 참조하는 타입의 경우 해당 객체 존재 체크 추가.
    - find 계열 중 사용하지 않는 메소드 제거.
    - isUnique 계열은 noneMatch로 변경.
    - @Service와 @RequiredArgsConstructor 사용.(Basic* 계열만)
    - Service 패키지의 JCF와 File 제거(스프링 DI로 repo의 저장 방식 선택)
    - RequiredArgsConstructor을 통해 DI 자동 주입.
    - DTO 도입에 따른 파라미터 및 반환값 변경.
    - delete 시 연관 도메인까지 함께 정리하는 정책 적용.
    - DTO 검증과 service 검증을 분리.

- 프로젝트 마이그레이션을 하면서 리펙토링 및 가독성 확보
- 실행파일에 Spring context를 이용하여 service DI시행.
- String.equals를 Objects.equals로 null-safe 추가.

</details>

<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 4</summary>

### Sprint 4

- sprint 4의 요구 사항인 엔드포인트 구현.
- 멘토님의 리뷰를 참고하여 기존 코드 수정
    - RequestMapping을 이용하여 get과 post, put, delete 엔드포인트 구현
    - postman으로 요청값을 받기 위해 restController로 구현.
    - 첨부파일을 받는 경우를 위해 MultipartFile를 사용.(create만 사용.)
    - 전역 예외 처리를 위해 globalException 구현

---

- 수정 사항
    - DTO
        - 일관성을 위해 네이밍 수정
        - 심화 요구사항을 실행하기 위해 필드명 수정 및 필드 추가

    - 엔티티
        - 일관성을 위해 binaryContent의 필드명 수정.

    - 레포지토리
        - 경로를 하드코딩이 아닌 @Value를 통해 주입
            - 그에 따른 path를 생성자에서 초기화
        - 객체를 사용하지 않는 존재 체크의 경우 boolean으로 존재 체크로 변경.

    - 서비스
        - 변수나 필드명 축약하지 않고 기존 단어 유지
        - 내부 검증만 하는 메서드를 private로 전환
        - optional의 경우 조건문으로 예외 처리가 아닌 orElse로 수정.

</details>

<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 5</summary>

### Sprint 5

- 코드 컨벤션을 Google Java Style로 변경
- ReentrantLock를 통한 원자성 확보
- 주어진 API 스펙에 맞게 재구현
- 멘토님의 리뷰를 참고하여 코드 수정
    - 컨트롤러의 파라미터중 @Parameter -> @RequestParam으로 수정
    - Custom Exception 추가 및 httpStatus 세분화
    - GlobalException의 단순 출력부분을 @Slf4j를 사용하여 로깅 부분 수정
    - 컨트롤러에서 반환하는 status 수정
    - 패키지명 수정

- Interceptor/ArgumentResolver 관련
    - 기존에는 객체 삭제 시 권한 검증을 위해 owner 필드를 사용했으나
      현재 요구사항 및 API 스펙에서는 별도의 인증/권한 로직이 필요하지 않음.
    - 또한 login API는 단순히 User를 반환하는 구조이므로, session 기반 처리 역시 제거.
    - 이에 따라 Interceptor/ArgumentResolver는 적용하지 않았으며,
      추후 인증/권한 요구사항이 추가될 경우 해당 구조로 리팩토링 예정.

---

- 수정 사항
    - Dto
        - 응답 DTO를 API 요구 스펙에 맞게 변경
        - 쓰지 않는 Dto 제거
        - 요구하는 필드 추가 및 네이밍 변경

    - 엔티티
        - 요구하는 필드 추가 및 네이밍 변경

    - 예외
        - API 요청 검증 실패 시 400 Bad Request 반환하도록 예외 처리 수정
        - CustomException 추가(NotFound, Duplicate, Unauthorized)
        - status 코드에 따라 http status 세분화

    - 레포지토리
        - ReentrantLock를 사용

    - 서비스
        - 반환값을 API 요구 스펙에 따라 DTO에서 엔티티로 변경
        - API 스펙에 없는 사용하지 않는 메서드 삭제

</details>

<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 6</summary>

### Sprint 6

- PostgreSQL 연동을 위한 Spring Data JPA 기반 Repository 구성
- 기존 File/JCF Repository 기반 구조를 JPA 기반 DB 연동 구조로 변경
- 엔티티 중복 필드를 줄이기 위해 BaseEntity를 구성하고 상속 구조 적용
- JPA 연관관계 매핑을 통해 엔티티 간 관계 정의
- API 응답에서 Entity 직접 노출을 줄이기 위해 DTO 도입
- BinaryContent의 bytes 데이터를 DB에 저장하지 않고 별도 Storage로 분리
- 메시지 목록 조회에 페이징 적용

- 심화
    - N+1 완화를 위해 entitygraph 혹은 fetch join 적용
    - 페이징 방식에서 슬라이스방식으로 변경
    - transactional에 readonly 적용
    - MapStruct 적용

</details>

<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 7</summary>

### Sprint 7

- 프로파일 기반 설정을 위한 YAML 분리
- 주요 서비스 및 컨트롤러 레이어에 로깅 구현
- logback을 통한 일관성 있는 로깅
- 도메인별 커스텀 예외와 공통 예외 응답 처리를 구현
- Dto 기반 Bean Validation 적용
- Actuator 적용
- TDD 기반 서비스, 슬라이스, 통합 테스트 추가
- 멘토님의 리뷰를 참고하여 코드 수정
    - 기존 요구사항의 RequestMapping -> 해당 method의 mapping
    - 컨트롤러의 반환값을 인터셉터로 중복을 줄이는 부분은 추후 개선 예정
    - 기존 sprint6의 setter는 dirty checking을 위해 유자

- 심화
    - MDC 기반 로깅을 위한 인터셉터와 Logback 패턴을 구현
    - Spring Boot Admin 모듈 및 Actuator 기반 모니터링 연동 구성
    - JaCoCo 기반 테스트 커버리지 리포트 생성 및 커버리지 개선

</details>

<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 8</summary>

### Sprint 8

- Docker 기반 애플리케이션 실행 환경 구성
- Docker Compose를 통해 애플리케이션과 db 컨테이너를 올려 테스트 환경 구축
- AWS S3를 통한 클라우드 storage 구축
- AWS RDS를 통한 DB 구축
- EC2에 SSH 접근을 통한 RDS 접근 및 DB 설정
- 퍼블릭 ECR 구성
- ECS 구성
    - 클러스터 -> 태스크 정의 -> 서비스 생성
    - 태스크 인바운드 규칙 설정

- 심화
    - 멀티 스테이지를 활용하여 이미지 크기 감소
      ```aiignore
      # 런타임 x                         | 1.4gb
      # 런타임 amazoncorretto:17         | 850mb
      # 런타임 amazoncorretto:17-al2023  | 830mb
      # 런타임 amazoncorretto:17-alpine  | 600mb
      ```
    - Github Actions를 통한 CI/CD 구축
    - CI로 테스트 코드 실행을 한 후 CodeCov로 커버리지 체크
    - CD를 통해 이미지를 빌드하고 해당 이미지로 서비스 업데이트

</details>

<details>
<summary style="font-size: 20px; font-weight: bold;">Sprint 9</summary>

### Sprint 9

- 멘토님의 리뷰를 참고하여 수정
    - S3.delete()에 try-catch를 추가하여 예외 처리 패턴 일관화
    - 정합성 부분 해결 시도
        - DB가 커밋 성공 후 s3 정리 하는 방식으로 진행
            - 이벤트 기반으로 `AFTER_COMMIT`후 `REQUIRES_NEW`이 아닌 트랜잭션동기화 방식으로 시도.
    - AWS 환경변수를 `env:` 블록에 넣는 방식으로 변경
    - 메시지 엔티티의 `orphanRemoval`로 DB는 삭제 되지만 실제 파일은 다른 곳에 책임이 있음.
        - `BinaryContent`는 메타데이터, `Storage`에서 실제 파일을 관리한다고 주석으로 처리.
    - `.dockerignore`부분은 추후 추가하겠습니다.
    - s3에서 `put()`동작시 contentType도 추가하여 저장하도록 변경
    - postgreSQL 버전을 17로 변경
- 스프링 시큐리티 적용
    - csrf 보호
    - 로그인, 로그아웃
    - 권한에 따른 접근 제어
- 사용자에 대해 Role을 추가
    - `ADMIN`, `CHANNEL_MANAGER`, `USER`
    - 스키마에 role 추가
    - 디폴트 값으로 USER 적용
    - 실행시 `ADMIN`이 없으면 어드민 계정을 초기화
    - 채널 생성, 수정, 삭제를 `CHANNEL_MANAGER`에 부여
    - 사용자 권한 설정은 `ADMIN`에 부여
    - `RoleHierarchy`를 활용해 권한의 계층 부여

- 심화
    - 세션 설정으로 동일한 계정으로 로그인 제어
    - 권한이 변경된 사용자의 세션 무효화
    - `UserStatus`제거 및 로그인 여부를 세션으로 관리
    - `Remember-me`를 이용하여 인증 유지
    - `SpEL`을 활용하여 사용자 수정, 삭제와 메시지 수정, 삭제는 본인만 가능

</details>