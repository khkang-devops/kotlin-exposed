package com.test.kotlinexposed.common.exposed

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

open class BaseTable(name: String) : Table(name) {
    val createId = varchar("create_id", 10).nullable()
    val createDttm = datetime("create_dttm").clientDefault { LocalDateTime.now() }
    val updateId = varchar("update_id", 10).nullable()
    val updateDttm = datetime("update_dttm").nullable()
}