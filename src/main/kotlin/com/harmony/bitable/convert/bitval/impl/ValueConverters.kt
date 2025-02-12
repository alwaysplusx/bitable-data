package com.harmony.bitable.convert.bitval.impl

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

    fun <T> convertObject(value: Any?, type: Class<*>): T? {
        if (value == null || type.isInstance(value)) {
            return value as T
        }
        return Jsons.DEFAULT.fromJson(Jsons.DEFAULT.toJsonTree(value), type) as T
    }

    fun <T> convertArray(value: Any?, type: Class<T>): Array<T>? {
        if (value == null) {
            return null
        }
        return Jsons.DEFAULT.fromJson(Jsons.DEFAULT.toJsonTree(value), TypeToken.getArray(type)) as Array<T>
    }

    fun convertTime(value: Any?, type: Class<*>): Any? {
        if (value == null) {
            return null
        }
        val valueAsLong = (value as Double).toLong()
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


}