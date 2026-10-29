# file-archiver

검사 설비가 만든 검사 파일을 모아 박스·출하 단위로 압축하고, 출하 시점에 모든 제품의 검사 데이터를 zip 하나로 내려받게 하는 시스템.

- **server (이 레포):** 박스 포장·출하, zip 생성, 다운로드 URL 발급
- **file-agent (별도 레포):** 설비 PC에서 폴더를 감시해 검사 파일을 스토리지(MinIO)에 올리고 서버에 등록한다. 서버와는 API로만 연결된다

## 기술 스택

Java 21, Spring Boot, PostgreSQL, MinIO(S3 호환), Gradle, Docker Compose, Testcontainers

## 구조

```
file-archiver/
├── settings.gradle
├── build.gradle
├── docker-compose.yml     # PostgreSQL, MinIO
└── src/main/java/com/filearchiver/
    ├── packing/           # 검사 파일 등록·포장·출하·제품 (비즈니스 규약)
    ├── file/              # storage 읽기·쓰기, zip 생성·업로드
    ├── worker/            # PENDING 작업 처리, 재처리
    └── config/            # 설정, 기동 시 초기화
```

덩어리 안은 presentation, application, domain, infrastructure 계층으로 나눈다.
