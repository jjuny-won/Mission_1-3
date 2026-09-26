# [1부 미션] 회원과 글 모듈 분리하기

Spring Boot와 Spring Data JPA로 회원, 글, 댓글을 만들고, 이벤트, HTTP API, 회원 복제본을 이용해 회원 모듈과 글 모듈을 분리합니다.

## 1. 실행 방법

| 항목 | 내용 |
|---|---|
| JDK | 25 |
| Spring Boot | 4.1.1 |
| DB | H2 파일 모드 (별도 설치 불필요) |

**실행 명령**

```bash
./gradlew bootRun
```

처음 상태로 다시 확인하려면 애플리케이션을 종료한 뒤 `rm db_dev*` 후 재실행합니다.

**DB 설정** (`src/main/resources/application-dev.yaml`)

```yaml
spring:
  datasource:
    url: jdbc:h2:./db_dev;MODE=MySQL
    username: sa
    password:
    driver-class-name: org.h2.Driver
```

- `application.yaml`에서 `dev` 프로필을 활성화하므로 `application-dev.yaml`이 자동으로 적용됩니다.
- DB 파일은 프로젝트 루트에 `db_dev.mv.db`로 생성되며 `.gitignore`로 제외했습니다.
- 재실행 시 데이터가 유지되도록 `ddl-auto: update`를 사용합니다.

**H2 콘솔**

- 주소: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:./db_dev` / User: `sa` / Password: (없음)

## 2. 구조 설명

### 2-1. 모듈 구성

```
com.mission
├── global
│   ├── eventPublisher    EventPublisher
│   ├── exception         DomainException
│   ├── global            GlobalConfig
│   ├── jpa               BaseEntity, BaseIdAndTime
│   └── rsData            RsData
├── shared
│   ├── member
│   │   ├── dto           MemberDto
│   │   ├── event         MemberJoinedEvent, MemberModifiedEvent
│   │   └── out           MemberApiClient
│   └── post
│       ├── dto           PostDto, PostCommentDto
│       └── event         PostCreatedEvent, PostCommentCreatedEvent
└── boundedContext
    ├── member
    │   ├── in            ApiV1MemberController, MemberEventListener, MemberDataInit
    │   ├── app           MemberFacade, MemberJoinUseCase, MemberIncreaseActivityScoreUseCase,
    │   │                 MemberGetRandomSecureTipUseCase, MemberSupport
    │   ├── domain        Member, MemberPolicy
    │   └── out           MemberRepository
    └── post
        ├── in            PostEventListener, PostDataInit
        ├── app           PostFacade, PostWriteUseCase, PostAddCommentUseCase,
        │                 PostSyncMemberUseCase, PostSupport
        ├── domain        Post, PostComment, PostMember
        └── out           PostRepository, PostMemberRepository
```

| 계층 | 역할 |
|---|---|
| `in` | 입력(HTTP 요청, 이벤트, 초기화)을 받아 자기 모듈의 Facade만 호출 |
| `app` | Facade는 트랜잭션 관리, UseCase는 업무 로직, Support는 단순 조회 |
| `domain` | 엔티티와 도메인 규칙, 정책(`MemberPolicy`) |
| `out` | Repository |

- member와 post는 서로의 패키지를 import하지 않고, `shared`의 이벤트, DTO, ApiClient로만 통신합니다.
- 초기화는 `MemberDataInit`(`@Order(1)`) → `PostDataInit`(`@Order(2)`) 순서로 실행되며, 글 초기화는 `PostFacade`로 복제본(PostMember)을 조회해 작성자로 사용합니다.
- 댓글은 `Post.addComment()`로 생성하고 `cascade = PERSIST`로 글과 함께 저장합니다. 별도의 댓글 저장 호출은 없습니다.

### 2-2. 이벤트와 HTTP API를 구분한 이유

| 방식 | 사용처 | 선택 이유 |
|---|---|---|
| 이벤트 | 글/댓글 작성 → 활동점수 증가<br>회원 가입/수정 → 복제본 생성·갱신 | "일이 일어났다"는 알림. 발행하는 쪽은 결과를 기다리지 않으며, 점수 반영 실패가 글 작성 실패로 이어지면 안 됨 |
| HTTP API | 글 작성 시 보안 팁 조회 | 결과 메시지에 넣을 값을 지금 받아야 하는 조회. 이벤트는 응답을 돌려받을 수 없음 |
| 복제본 | 글과 댓글의 작성자 (PostMember) | 자주 필요한 회원 데이터를 자기 모듈에 두어, 매번 회원 모듈에 묻지 않고 JPA 연관관계로 참조 |

보안 팁을 복제하지 않고 HTTP로 조회한 이유는, 팁이 `MemberPolicy`의 정책(비밀번호 변경 주기 90일)에서 만들어지는 값이기 때문입니다. 복제하면 정책 로직이 두 모듈에 중복됩니다. `MemberApiClient`는 설정값 `custom.global.internalBackUrl`로 호출하므로, 회원 모듈을 별도 서버로 분리해도 주소만 바꾸면 됩니다.

### 2-3. 회원 복제 흐름

```
[가입]
MemberFacade.join() 커밋
   ↓ MemberJoinedEvent(MemberDto)
PostEventListener → PostFacade.syncMember() → PostMember INSERT

[활동점수 변경]
PostFacade.write() / addComment() 커밋
   ↓ PostCreatedEvent / PostCommentCreatedEvent
MemberEventListener → MemberFacade.increaseActivityScore(authorId, 3 / 1)
   → 점수 변경 → flush → MemberModifiedEvent(MemberDto) 발행 → 커밋
   ↓
PostEventListener → PostFacade.syncMember() → PostMember UPDATE
```

- 복제 항목은 ID, username, 닉네임, 활동점수, 생성 시각, 수정 시각입니다. `MemberDto`와 `PostMember`에 비밀번호 필드가 없으므로 비밀번호는 복제되지 않습니다.
- `PostMember`는 원본과 같은 ID와 원본 시각을 그대로 저장하기 위해 `@GeneratedValue`와 Auditing을 쓰지 않습니다.
- 가입과 수정이 같은 `syncMember()`를 사용합니다. ID가 있는 엔티티를 `save()`하면 merge가 수행되어, 행이 없으면 INSERT(가입), 있으면 UPDATE(점수 변경)가 되므로 갱신 시 행이 추가되지 않습니다.
- 수정 시각(`@LastModifiedDate`)은 flush 시점에 채워지므로, 점수 변경 후 flush로 수정 시각을 확정한 뒤 DTO를 만들어 이벤트를 발행합니다. 그래야 복제본의 수정 시각이 원본과 일치합니다.
- 점수 변경과 복제는 `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Transactional(propagation = REQUIRES_NEW)`로 처리해, 작성 트랜잭션이 커밋된 뒤 별도 트랜잭션에서 저장합니다. 작성이 롤백되면 점수도 오르지 않습니다.
- 글과 댓글의 작성자는 원본 `Member`가 아닌 `PostMember`를 참조합니다.

## 3. 확인 결과

DB 파일을 삭제하고 최초 실행한 뒤 H2 콘솔과 애플리케이션 로그에서 확인했습니다.

### 3-1. 초기 데이터 개수

```sql
SELECT (SELECT COUNT(*) FROM MEMBER)            AS MEMBER_CNT,
       (SELECT COUNT(*) FROM POST)              AS POST_CNT,
       (SELECT COUNT(*) FROM POST_POST_COMMENT) AS COMMENT_CNT;
```

```
MEMBER_CNT  POST_CNT  COMMENT_CNT
6           6         8
(1 row)
```

### 3-2. 회원별 글 수, 댓글 수, 활동점수

```sql
SELECT pm.USERNAME,
       (SELECT COUNT(*) FROM POST p WHERE p.AUTHOR_ID = pm.ID)              AS POST_CNT,
       (SELECT COUNT(*) FROM POST_POST_COMMENT c WHERE c.AUTHOR_ID = pm.ID) AS COMMENT_CNT,
       m.ACTIVITY_SCORE
FROM MEMBER m JOIN POST_MEMBER pm ON m.ID = pm.ID
ORDER BY pm.ID;
```

```
USERNAME  POST_CNT  COMMENT_CNT  ACTIVITY_SCORE
system    0         0            0
holding   0         0            0
admin     0         0            0
user1     3         2            11
user2     2         3            9
user3     1         3            6
(6 rows)
```

모든 회원의 활동점수가 `글 수 × 3 + 댓글 수 × 1`과 일치합니다.

### 3-3. 원본과 복제본의 일치

원본(MEMBER)과 복제본(POST_MEMBER)에서 값이 다른 행을 조회합니다. 0건이면 ID, username, 닉네임, 활동점수, 생성 시각, 수정 시각이 모두 일치합니다.

```sql
SELECT ID, USERNAME, NICKNAME, ACTIVITY_SCORE, CREATE_DATE, MODIFY_DATE FROM MEMBER
EXCEPT
SELECT ID, USERNAME, NICKNAME, ACTIVITY_SCORE, CREATE_DATE, MODIFY_DATE FROM POST_MEMBER;
```

```
ID  USERNAME  NICKNAME  ACTIVITY_SCORE  CREATE_DATE  MODIFY_DATE
(no rows)
```

점수가 여러 번 갱신된 뒤에도 복제본은 6행으로, 갱신 시 행이 추가되지 않았습니다.

```sql
SELECT COUNT(*) AS POST_MEMBER_CNT FROM POST_MEMBER;
```

```
POST_MEMBER_CNT
6
(1 row)
```

복제본 테이블에는 비밀번호 컬럼이 없습니다.

```sql
SHOW COLUMNS FROM POST_MEMBER;
```

```
FIELD           TYPE                    NULL  KEY
ID              INTEGER                 NO    PRI
ACTIVITY_SCORE  INTEGER                 NO
CREATE_DATE     TIMESTAMP(6)            YES
MODIFY_DATE     TIMESTAMP(6)            YES
NICKNAME        CHARACTER VARYING(255)  YES
USERNAME        CHARACTER VARYING(255)  YES   UNI
(6 rows)
```

### 3-4. 재실행 시 중복 없음

DB 파일을 삭제하지 않고 애플리케이션을 한 번 더 실행한 뒤, 3-1과 3-2 쿼리를 다시 실행했습니다. 회원과 글은 `count() > 0`, 댓글은 `post1.hasComments()`이면 초기화를 건너뛰므로, 가입과 작성이 다시 일어나지 않고 이벤트도 발행되지 않습니다.

```
MEMBER_CNT  POST_CNT  COMMENT_CNT
6           6         8
(1 row)
```

```
USERNAME  POST_CNT  COMMENT_CNT  ACTIVITY_SCORE
system    0         0            0
holding   0         0            0
admin     0         0            0
user1     3         2            11
user2     2         3            9
user3     1         3            6
(6 rows)
```

첫 실행과 개수, 활동점수가 모두 동일하며, 두 번째 실행 로그에는 회원, 글, 댓글의 `insert` 문이 없습니다.

### 3-5. 보안 팁 호출 결과

**API 직접 호출**

```bash
curl -i http://localhost:8080/api/v1/member/members/randomSecureTip
```

```
비밀번호의 유효기간은 90일 입니다.
```

**글 작성 결과 메시지** (`PostDataInit` 로그)

글 모듈은 글을 작성할 때 `MemberApiClient`로 위 API를 실제 HTTP 호출하고, 받은 보안 팁을 결과 메시지에 포함합니다.

```
DEBUG 25272 --- [mission] [  restartedMain] c.m.boundedContext.post.in.PostDataInit  : 1번 글이 생성되었습니다. 보안 팁 : 비밀번호의 유효기간은 90일 입니다.
```

보안 팁의 90일은 `MemberPolicy`가 설정값 `custom.member.password.changeDays`에서 읽어 옵니다.