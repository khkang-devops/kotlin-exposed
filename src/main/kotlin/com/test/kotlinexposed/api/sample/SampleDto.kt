package com.test.kotlinexposed.api.sample

import org.jetbrains.exposed.sql.ResultRow
import java.sql.ResultSet
import java.time.LocalDateTime

data class SampleRequestDto(
    val sampleId: String? = null,
    val sampleName: String? = null
)

data class SampleResponseDto(
    val sampleId: String? = null,
    val sampleName: String? = null,
    val createId: String? = null,
    val createDttm: LocalDateTime? = null,
    val updateId: String? = null,
    val updateDttm: LocalDateTime? = null
) {
    constructor(row: ResultRow): this(
        sampleId = row[Sample.sampleId],
        sampleName = row[Sample.sampleName],
        createId = row[Sample.createId],
        createDttm = row[Sample.createDttm],
        updateId = row[Sample.updateId],
        updateDttm = row[Sample.updateDttm]
    )
    constructor(row: ResultSet): this(
        sampleId = row.getString("sample_id"),
        sampleName = row.getString("sample_name"),
        createId = row.getString("create_id"),
        createDttm = row.getTimestamp("create_dttm").toLocalDateTime(),
        updateId = row.getString("update_id"),
        updateDttm = row.getTimestamp("update_dttm")?.toLocalDateTime()
    )
}