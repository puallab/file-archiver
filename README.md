# file-archiver

검사 설비가 만든 검사 파일을 모아 박스·출하 단위로 압축하고, 출하 시점에 모든 제품의 검사 데이터를 zip 하나로 내려받게 하는 시스템.

- **server (이 레포):** 박스 포장·출하, zip 생성, 다운로드 URL 발급
- **file-agent (별도 레포):** 설비 PC에서 폴더를 감시해 검사 파일을 스토리지(MinIO)에 올리고 서버에 등록한다. 서버와는 [API 계약](docs/api.md)으로만 연결된다

## 기술 스택

Java 21, Spring Boot, PostgreSQL, MinIO(S3 호환), Gradle, Docker Compose, Testcontainers

## 구조

```
file-archiver/
├── settings.gradle
├── build.gradle
├── docker-compose.yml     # PostgreSQL, MinIO
├── docs/
└── src/main/java/com/filearchiver/server/
    ├── file/              # storage 읽기·쓰기, zip 생성·업로드
    ├── inspection/        # 검사 파일 등록
    ├── packing/           # 포장·출하·제품 (shipment·packaging·product)
    ├── job/               # PENDING 작업을 집어 가는 Worker, 재처리
    └── global/            # 공통 설정, 예외 처리
```

## 문서

| 문서 | 내용 |
|---|---|
| [erd.md](docs/erd.md) | ERD와 테이블 정의 |
| [architecture.png](docs/architecture.png) | 전체 구성도 |
