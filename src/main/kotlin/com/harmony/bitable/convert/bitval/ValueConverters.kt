package com.harmony.bitable.convert.bitval

import com.google.gson.reflect.TypeToken
import com.lark.oapi.core.utils.Jsons
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * @author wuxin
 */
object ValueConverters {

    fun <T> convertToObject(value: Any?, type: Class<*>): T? {
        if (value == null || type.isInstance(value)) {
            return value as T
        }
        return Jsons.DEFAULT.fromJson(Jsons.DEFAULT.toJsonTree(value), type) as T
    }

    fun <T> convertToArray(value: Any?, type: Class<T>): Array<T>? {
        if (value == null) {
            return null
        }
        return Jsons.DEFAULT.fromJson(Jsons.DEFAULT.toJsonTree(value), TypeToken.getArray(type)) as Array<T>
    }

    fun convertToTime(value: Number, type: Class<*>): Any? {
        val valueAsLong = value.toLong()
        return when (type) {

            Long::class.java -> valueAsLong

            LocalDateTime::class.java -> LocalDateTime.ofInstant(
                Instant.ofEpochMilli(valueAsLong), ZoneId.systemDefault()
            )

            LocalDate::class.java -> LocalDate.ofInstant(
                Instant.ofEpochMilli(valueAsLong), ZoneId.systemDefault()
            )

            else -> {
                throw IllegalArgumentException("unsupported type: $type")
            }
        }
    }

    fun convertToLong(value: Any?): Long? {
        if (value == null) {
            return null
        }
        return when (value) {
            is Number -> value.toLong()
            is LocalDate -> value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            is LocalDateTime -> value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            else -> {
                throw IllegalArgumentException("unsupported value: $value")
            }
        }
    }

}