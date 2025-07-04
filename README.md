## Kotlin + Springboot + Exposed

## Application Stack
* Language : Kotlin JVM 1.9.25
* Spring Boot : 3.5.3
* JDK : azul/zulu-17
* Gradle : 8.14.2
* Exposed 0.61.0

---

## Kotlin Exposed
- JetBrains 에서 개발한 Kotlin 기반의 경량 ORM(객체-관계 매핑) 및 SQL 라이브러리
- Kotlin 생태계에서 SQL 빌더로서의 표준적인 위치를 차지
- JDBC 위에서 동작하며 불필요한 오버헤드가 적고, 코드가 가벼움
- Kotlin 친화적이며 SQL 쿼리와 테이블 정의를 Kotlin 코드로 자연스럽게 작성할 수 있음
- 두 가지 접근 방식 제공
  - DSL (Domain-Specific Language) 방식
    - SQL과 유사한 형태로 쿼리를 작성할 수 있어, SQL의 자유로움과 Kotlin의 타입 안정성을 동시에 누릴 수 있음
  - DAO (Data Access Object) 방식
    - 객체지향적으로 데이터베이스 엔티티를 다루는 방식으로, Hibernate 등 전통적인 ORM 과 유사하게 사용할 수 있음

## Dependencies
```kotlin
implementation("org.jetbrains.exposed:exposed-core:0.61.0")
implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")
implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.61.0")
```

## Spring Datasource 설정
```yaml
# application.yml
spring:
  datasource:
    driver-class-name: driver
    url: url
    username: username
    password: password
```

## 테이블 생성
```kotlin
object Sample : Table("sample") {
    val sampleId = varchar("sample_id", 10)
    val sampleName = varchar("sample_name", 10).nullable()

    override val primaryKey = PrimaryKey(sampleId)
}
```

## 공통 테이블 상속 생성
```kotlin
// 공통 테이블 정의
open class BaseTable(name: String) : Table(name) {
    val createId = varchar("create_id", 10).nullable()
    val createDttm = datetime("create_dttm").clientDefault { LocalDateTime.now() }
    val updateId = varchar("update_id", 10).nullable()
    val updateDttm = datetime("update_dttm").nullable()
}

// 공통 테이블 상속 생성
object Sample : BaseTable("sample") {
    val sampleId = varchar("sample_id", 10)
    val sampleName = varchar("sample_name", 10).nullable()

    override val primaryKey = PrimaryKey(sampleId)
}
```

## 기본 쿼리 (DSL 방식)
```kotlin
// select
Table
    .selectAll()
    .where(condition.add(Table.id eq "id"))

// count
Table
    .selectAll()
    .where(condition.add(Table.id eq "id"))
    .count()

// insert
Table.insert {
    it[id] = "id"
    it[name] = "name"
    it[createId] = "createId"
    it[createDttm] = LocalDateTime.now()
}

// update
Table.update({ Table.id eq "1" }) {
    it[name] = "name"
    it[updateId] = "updateId"
    it[updateDttm] = LocalDateTime.now()
}

//delete
Table.deleteWhere { Table.id eq "1" }
```

## Native 쿼리
```kotlin
fun getSampleListNative(request: SampleRequestDto): List<SampleResponseDto> {
    val list = mutableListOf<SampleResponseDto>()
    val conditions = mutableListOf<String>()
    val params = mutableListOf<Pair<IColumnType<*>, Any?>>()

    // where
    if (request.sampleId != null) {
        conditions.add("sample_id = ?")
        params.add(Sample.sampleId.columnType to request.sampleId)
    }
    if (request.sampleName != null) {
        conditions.add("sample_name like ?")
        params.add(Sample.sampleName.columnType to "%${request.sampleName}%")
    }

    // sql
    val sql = buildString {
        append("""
            select
                *
            from
                sample
        """.trimIndent())

        if (conditions.isNotEmpty()) {
            append(" where ")
            append(conditions.joinToString(" and "))
        }
    }

    // execute
    TransactionManager.current().exec(sql, params) {
        while (it.next()) {
            list.add(SampleResponseDto(it))
        }
    }

    // return
    return list
}
```

## Spring + Exposed 트랜잭션 연동
- exposed-spring-boot-starter 모듈을 사용하면 spring 에서 관리하는 트랜잭션 매니저 사용 가능
- service 단에서 @Transactional 을 사용하면 exposed 의 transaction{} 블럭 없이도 트랜잭션 관리 가능
```kotlin
@Transactional
fun insertSample(request: SampleRequestDto) {
    // exposed insert 모듈 호출
    sampleRepository.insertSample(request)
}
```

## 개인적인 JPA 와의 비교
- JPA 에서 커스텀 쿼리 구현시,
  - QueryDSL 과 같은 모듈을 추가적으로 설치 설정해야 하는 불편함 존재
  - 커스텀 interface 생성 후 커스텀 interface 를 구현하는 Impl class 를 생성해야 하는 구조적 불편함
  - QueryDSL 사용 시 코드에 QClass 명시해야 하는 불편함
  - 쿼리 결과를 담는 response dto 를 @QueryProjection 으로 생성해야 하는 불편함
- Exposed 사용 시 위의 불편함이 해소되며 코드도 좀 더 간결해짐
- JDBC 위 thin wrapper 로 동작해, 불필요한 오버헤드가 적고, 코드가 가벼움
- 쿼리 결과가 dto 에 자동 매칭이 안되는 점은 아쉬움

  | 항목     | JPA          | Exposed           |
  |--------|--------------------------|-------------------|
  | 언어 친화성 | Java 중심, Kotlin 과 궁합이 나쁨 | Kotlin 전용, Kotlin 문법 적극 활용 |
  | 쿼리 제어 | 추상화 높음, SQL 직접 제어 어려움    | SQL DSL로 직접 제어, 투명성 높음 |
  | 성능 | 복잡한 쿼리/대량 처리에서 오버헤드 있음   | Batch 등에서 더 나은 성능 가능 |
  | 코드 가독성/간결성 | 설정, 엔티티, 매핑 등 보일러플레이트 많음 | DSL로 간결, 타입 안전성 높음 |
