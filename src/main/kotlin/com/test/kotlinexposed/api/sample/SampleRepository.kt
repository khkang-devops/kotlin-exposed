package com.test.kotlinexposed.api.sample

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class SampleRepository {
    // 샘플리스트
    fun getSampleList(request: SampleRequestDto): Query {
        return Sample.selectAll()
            .where(getCondition(request))
    }

    // 샘플리스트카운트
    fun getSampleListCount(request: SampleRequestDto): Long {
        return Sample.selectAll()
            .where(getCondition(request))
            .count()
    }

    // 조회조건
    fun getCondition(request: SampleRequestDto): Op<Boolean> {
        var condition: Op<Boolean> = Op.TRUE

        if (request.sampleId != null) {
            condition = condition and (Sample.sampleId eq request.sampleId)
        }
        if (request.sampleName != null) {
            condition = condition and (Sample.sampleName like "%${request.sampleName}%")
        }

        return condition
    }

    // 샘플조회
    fun getSample(sampleId: String): ResultRow? {
        return Sample.selectAll()
            .where(Sample.sampleId eq sampleId)
            .firstOrNull()
    }

    // 샘플등록
    fun insertSample(request: SampleRequestDto) {
        Sample.insert {
            it[sampleId] = request.sampleId!!
            it[sampleName] = request.sampleName
            it[createId] = "sample"
            it[createDttm] = LocalDateTime.now()
        }
    }

    // 샘플수정
    fun updateSample(request: SampleRequestDto) {
        Sample.update({ Sample.sampleId eq request.sampleId!! }) {
            it[sampleName] = request.sampleName
            it[updateId] = "sample"
            it[updateDttm] = LocalDateTime.now()
        }
    }

    // 샘플삭제
    fun deleteSample(sampleId: String) {
        Sample.deleteWhere { Sample.sampleId eq sampleId }
    }

    // 샘플리스트 (네이티브쿼리)
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
}