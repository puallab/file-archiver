# file-archiver ERD

### inspection_file: 업로드된 검사 파일 (재검사 포함)

| 컬럼 | 타입 | 키 | NULL | 설명 |
|---|---|---|---|---|
| file_id | bigint | PK | N | 대리키 |
| product_id | bigint | FK → product, UK① | N | 제품 |
| inspected_at | timestamp | UK① | N | 검사 시각 |
| sensor_id | varchar | | Y | 첫 공정 설비 값, 참고용 |
| uploader_equipment | varchar | | Y | 업로드한 마지막 공정 설비, 참고용 |
| file_path | varchar | | N | 스토리지 key |
| created_at | timestamp | | N | 등록 시각. 어떤 포장에도 쓰이지 않은 파일을 정리하는 기준 |

UK① = UNIQUE(product_id, inspected_at). 제품별 최신 파일 조회와 중복 등록 확인에 써요. 등록은 애플리케이션 조회로 기존 행을 먼저 찾고, 동시에 들어온 중복은 이 제약이 막아요. 같은 키로 다시 오면 제품이 포장 전일 때만 sensor_id·uploader_equipment·file_path를 바꿔요.

### product: 제품 1개당 1행, 현재 상태

| 컬럼 | 타입 | 키 | NULL | 설명 |
|---|---|---|---|---|
| product_id | bigint | PK | N | 대리키 |
| serial | varchar | UK | N | 제품 시리얼 (제품당 유일) |
| packaging_id | bigint | FK → packaging | Y | 담긴 포장(박스 또는 가상 포장). NULL이면 미포장. 포장 중복 방지(조건부 UPDATE)의 기준. 인덱스 |
| file_id | bigint | | Y | 포장 시 고정한 검사 파일 (논리 참조) |

### packaging: 포장 단위 1건 (실물 박스 또는 가상 포장)

| 컬럼 | 타입 | 키 | NULL | 설명 |
|---|---|---|---|---|
| packaging_id | bigint | PK | N | 대리키 |
| packaging_no | varchar | UK | N | 화면 식별용 번호 (날짜-시퀀스) |
| type | varchar | | N | BOX(실물 박스) / VIRTUAL(출하 시 낱개 제품을 묶은 가상 포장) |
| status | varchar | | N | PENDING(대기, 시작 전) / PROCESSING(압축 중) / PACKED / FAILED / SHIPPED |
| shipment_id | bigint | FK → shipment | Y | 포함된 출하. 인덱스 |
| zip_key | varchar | | Y | 박스 zip 스토리지 key. BOX만 압축 완료 후 채우고, VIRTUAL은 항상 NULL |
| created_at | timestamp | | N | 생성 시각 |
| packed_at | timestamp | | Y | 박스 압축 완료 시각 (BOX만) |

### shipment: 출하 1건

| 컬럼 | 타입 | 키 | NULL | 설명 |
|---|---|---|---|---|
| shipment_id | bigint | PK | N | 대리키 |
| shipment_no | varchar | UK | N | 화면 식별용 번호 (날짜-시퀀스) |
| status | varchar | | N | PENDING(대기, 시작 전) / PROCESSING(압축 중) / COMPLETED / FAILED |
| total_units | int | | N | 포함된 packaging 수 (진행률 분모) |
| done_units | int | | N | 출하 zip에 반영된 packaging 수 (진행률 분자) |
| zip_key | varchar | | Y | 최종 zip 스토리지 key |
| created_at | timestamp | | N | 출하 요청 시각 |
| completed_at | timestamp | | Y | 압축 완료 시각 |