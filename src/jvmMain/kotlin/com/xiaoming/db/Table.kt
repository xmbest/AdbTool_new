package com.xiaoming.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object Table : IntIdTable("kv_table"){
    val k:Column<String> = varchar("k",50).uniqueIndex()
    val v:Column<String> = varchar("v",255)
}



class KeyValue(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KeyValue>(Table)
    var k by Table.k
    var v by Table.v
}