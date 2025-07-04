package com.test.kotlinexposed.api.sample

import com.test.kotlinexposed.common.exposed.BaseTable

object Sample : BaseTable("sample") {
    val sampleId = varchar("sample_id", 10)
    val sampleName = varchar("sample_name", 10).nullable()

    override val primaryKey = PrimaryKey(sampleId)
}