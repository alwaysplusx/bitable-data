package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.repository.UpdateCustomizer
import com.harmony.bitable.utils.SearchUtils
import com.lark.oapi.service.bitable.v1.model.Person
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.reflect.KCallable

/**
 * @author wuxin
 */
open class UpdateBuilder<T>() {

    protected val nameValueMap: MutableMap<NameInformation, Any?> = mutableMapOf()
    protected var updateBy: Person? = null
    protected var updateTime: Long? = null

    protected var customizer: UpdateCustomizer<T>? = null

    fun <R> set(field: NameFunction<T, R>, value: Any?): UpdateBuilder<T> {
        nameValueMap[NameInformation.of(field as KCallable<*>)] = value
        return this
    }

    fun <R> updateBy(person: Person, updateTime: LocalDateTime = LocalDateTime.now()): UpdateBuilder<T> {
        this.updateBy = person
        this.updateTime = updateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
        return this
    }

    fun withCustomizer(customizer: UpdateCustomizer<T>?): UpdateBuilder<T> {
        this.customizer = customizer
        return this
    }

    internal fun build(recordId: String, persistentEntity: BitablePersistentEntity<*>): UpdateRequest {
        val address = persistentEntity.getBitableAddress()
        val fieldValueMap = buildFieldValueMap(persistentEntity)
        return SearchUtils.buildUpdateRequest(address, recordId) { req, body ->
            customizer?.customize(req, body)

            req.recordId(recordId)
            body.fields(fieldValueMap)
            body.lastModifiedBy(updateBy)
            body.lastModifiedTime(updateTime)
        }
    }

    private fun buildFieldValueMap(persistentEntity: BitablePersistentEntity<*>): Map<String, Any?> {
        return nameValueMap.map { persistentEntity.getFieldByName(it.key) to it.value }
            .associate { it.first.getBitfieldName() to it.second }
    }

}