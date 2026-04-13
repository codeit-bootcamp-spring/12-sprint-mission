## 스프린트 2차

### 설계 의사결정
#### 1. BaseEntity 도입
* User, Channel, Message에서 공통적으로 `id`, `createdAt` 등의 필드가 반복됨
* 중복 제거 및 일관성 확보를 위해 `BaseEntity` 추상 클래스 도입

#### 2. DTO 분리
* 엔티티 생성 시 필요한 데이터가 증가할 가능성 고려
* Service / Repository 계층이 변경되더라도 검증 로직 일관성 유지 목적
* DTO를 통해 입력 데이터를 캡슐화

#### 3. 검증
* 일부 데이터 검증 실패 시 전체 흐름 중단을 방지하기 위해 `runSafely` 구조 도입
* 비즈니스 규칙(중복 검사 등)은 Service 계층에서 수행
* 서비스가 달라도 일관적인 이메일 형식 검증을 위한 `EmailVerifier` 클래스 구현
* `Optional` 대신 `IllegalArgumentException` 사용
* 단순성과 구현 속도를 우선

#### 4. 파일 저장 구조 분리
* 파일 로딩/저장 로직의 중복 제거를 위해 `FileSerializationUtil` 구현

---

## 스프린트 3차

### 설계 의사결정
* 일부 도메인 변수 제거 -> 디스코드를 분석해 개별적인 변수를 선언했지만 요구사항과 충돌되는 부분이 잦아 제거.
* BaseEntity 제거. 
  * 사유: 일부 객체는 수정 불가능한 도메인 모델로 간주. 따라서 updatedAt 필드는 정의x. 
  * updateAt이 없는 부모 클래스 -> 이점이 없음. 
  * 일부는 id와 createdAt만 추가 -> 예외가 생긴다고 생각.