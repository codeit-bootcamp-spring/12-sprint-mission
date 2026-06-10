## 질문 / 고민한 부분

### ErrorCode와 커스텀 예외 타입 분리

`ErrorCode` enum으로 에러 코드를 정의하는 것 자체는 예외 상황을 안정적으로 구분하고, 에러 분기 처리를 명확하게 하기 위한 장치라고 이해했습니다.

다만 `UserNotFoundException`, `ChannelNotFoundException`처럼 구체적인 Exception 타입까지 따로 정의하는 것은 안정성보다는 가독성, 표현력, 유지보수성 쪽 장점이 더 큰 것처럼 느껴졌습니다.

실무에서는 모든 `ErrorCode`마다 Exception 타입을 1:1로 전부 만드는 편인가요?  
아니면 중요한 비즈니스 예외만 별도 Exception 타입으로 만들고, 나머지는 공통 예외 + `ErrorCode` 조합으로 처리하는 편인가요?

제가 이해한 방향이 맞는지 궁금합니다.

### UserStatusCreate 관련 질문

미션 요구사항 때문에 `UserStatusCreate` 로직과 관련 `ErrorCode`, Exception 클래스를 일단 만들어 두었습니다.

그런데 다시 복습해보니 현재 비즈니스 흐름에서 `UserStatusCreate`가 직접적으로 필요한지 의문이 들었습니다.

`UserStatus`는 별도의 생성 흐름으로 다루기보다는, 회원 생성이나 접속 상태 갱신 과정에서 내부적으로 함께 관리되는 값에 가까운 것처럼 느껴졌습니다.

실무에서는 이런 경우에도 확장 가능성을 생각해서 `UserStatusCreate` 같은 비즈니스 로직을 미리 만들어 두는 편인가요?

아니면 현재 비즈니스 흐름에서 직접 필요하지 않다면 `UserStatusCreate` 로직과 관련 `ErrorCode`, Exception 클래스는 만들지 않고, 실제로 필요한 시점에 추가하는 편인가요?