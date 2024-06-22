package com.xiaoming.db

import androidx.compose.ui.res.useResource
import com.xiaoming.state.GlobalState
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DAOImpl : DAO {

    private val db by lazy {
        val file = File(File(GlobalState.sHomePath, "AdbTool"), "data.db")
        if (!file.exists()) {
            file.createNewFile()
            useResource("db/data.db") {
                it.copyTo(file.outputStream())
            }
        }
        val db = Database.connect("jdbc:sqlite:${file.absolutePath}", "org.sqlite.JDBC")
        transaction(db) {
            if (!SchemaUtils.checkCycle(Table)) {
                SchemaUtils.create(Table)
            }
        }
        db
    }

    override suspend fun getString(key: String, default: String): String {
        return transaction(db) {
            KeyValue.find {
                Table.k eq key
            }.last().v
        }
    }

    override suspend fun putString(key: String, value: String) {
        transaction(db) {
            KeyValue.find {
                Table.k eq key
            }.last().delete()

            KeyValue.new {
                k = key
                v = value
            }
        }
    }


    override suspend fun updateString(key: String, value: String) {
        transaction(db) {
            KeyValue.find {
                Table.k eq key
            }.count()
        }
    }

    override suspend fun findAll(): List<KeyValue> {
        return transaction(db) {
            KeyValue.all().toList()
        }
    }
}