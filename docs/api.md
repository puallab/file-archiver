# file-archiver API

2026-10-17 · dohyun

모든 경로는 `/api/v1`로 시작한다. 생성 요청은 `201 Created`와 생성된 ID만 돌려주고, 진행 상태는 조회 API로 본다. 압축은 비동기라 생성 직후 상태는 PENDING이다.

## 목록

| 메서드 | 경로 | 호출자 | 하는 일 | 관련 |
|---|---|---|---|---|
| POST | `/inspection-files` | file-agent | 검사 파일 등록 | FR-2, FR-3 |
| POST | `/packagings` | MES·웹 | 박스 포장 | FR-5~FR-8 |
| DELETE | `/packagings/{id}` | MES·웹 | 박스 포장 취소 | FR-9 |
| GET | `/packagings` | MES·웹 | 포장 목록 (페이징) | |
| GET | `/packagings/{id}` | MES·웹 | 포장 상세 | |
| POST | `/shipments` | MES·웹 | 출하 | FR-10~FR-12 |
| DELETE | `/shipments/{id}` | MES·웹 | 출하 취소 | FR-13 |
| GET | `/shipments` | MES·웹 | 출하 목록 (페이징) | |
| GET | `/shipments/{id}` | MES·웹 | 출하 상세 | |
| GET | `/shipments/{id}/progress` | 웹 | 출하 압축 진행률 (폴링용, 가볍게 분리) | FR-16 |
| GET | `/shipments/{id}/download-url` | 웹 | 최종 zip 다운로드 URL | FR-14, FR-15 |
| GET | `/products/{serial}` | MES·웹 | 제품 상태 조회 | |

## POST /inspection-files

file-agent가 스토리지에 파일을 올린 뒤 메타데이터를 등록한다.

요청

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| serial | string | Y | 제품 시리얼 |
| sensorId | string | N | 첫 공정 설비 값, 참고용 |
| inspectedAt | string | Y | 검사 시각, KST 기준 ISO-8601 로컬 시각. 오프셋 없음 (예: `2026-10-17T15:41:00`) |
| filePath | string | Y | 스토리지 key |
| uploaderEquipment | string | N | 업로드한 마지막 공정 설비, 참고용 |

응답

| 상황 | 코드 | 본문 |
|---|---|---|
| 등록됨 | 201 | `{"fileId": 123}` |
| 같은 (product_id, inspected_at)이 이미 있음 (재시도) | 201 | 기존 `fileId`. 실패로 보지 않는다 |
| 필수 필드 누락·형식 오류 | 400 | 오류 본문 |

처음 들어온 시리얼이면 같은 트랜잭션 안에서 product 행도 만든다.

## POST /packagings

요청

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| serials | string[] | Y | 박스에 담을 시리얼 목록. 비어 있거나 중복이 있거나 너무 길면 400 |

응답

| 상황 | 코드 | 본문 |
|---|---|---|
| 포장됨 (압축은 대기) | 201 | `{"packagingId": 45}` |
| 이미 다른 박스에 포장된 시리얼이 있음 (ALREADY_PACKED) | 409 | 오류 본문 + `rejected` |
| 검사 파일이 없는 시리얼이 있음 (NO_INSPECTION_FILE) | 422 | 오류 본문 + `rejected` |
| 목록이 비어 있음·중복·크기 초과 | 400 | 오류 본문 |

하나라도 거절되면 포장 전체를 거절한다. 409와 422 사유가 섞이면 정한 우선순위의 코드 하나로 응답하고, `rejected`에는 모든 시리얼을 사유와 함께 담는다.

거절 본문 예

```json
{
  "success": false,
  "message": "포장할 수 없는 시리얼이 있습니다",
  "rejected": [
    {"serial": "SN0001", "reason": "ALREADY_PACKED"},
    {"serial": "SN0002", "reason": "NO_INSPECTION_FILE"}
  ]
}
```

## GET /shipments/{id}/download-url

최종 zip이 완료된 출하만 받을 수 있다. URL은 JSON으로 돌려준다.

```json
{"url": "https://…", "expiresAt": "2026-10-17T16:41:00+09:00"}
```

## 미정

- 409와 422가 섞일 때 어느 코드를 우선할지
- `POST /shipments` 요청·응답 필드, FR-11 거절(압축 안 끝난 박스, 이미 출하된 박스)의 응답 코드
- 포장·출하 상세·목록, 진행률, 제품 조회의 응답 필드
- 오류 본문 형식을 지금 형태(`success`, `message`)로 갈지 RFC 9457 ProblemDetail로 갈지
