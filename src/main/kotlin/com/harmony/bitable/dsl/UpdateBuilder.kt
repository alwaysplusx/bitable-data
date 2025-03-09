package com.harmony.bitable.dsl

import com.harmony.bitable.core.UpdateRequest
import com.harmony.bitable.mapping.BitablePersistentEntity
import com.harmony.bitable.repository.UpdateCustomizer
import com.harmony.bitable.utils.SearchUtils
import com.lark.oapi.service.bitable.v1.model.Person
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.reflect.KFunction1
import kotlin.reflect.KMutableProperty1

/**
 * @author wuxin
 */
class UpdateBuilder<T>(private val rootType: Class<T>) {

    private val nameValueMap: MutableMap<NameInformation, Any?> = mutableMapOf()
    private var updateBy: Person? = null
    private var updateTime: Long? = null

    private var customizer: UpdateCustomizer<T>? = null

    fun <R> set(field: KFunction1<T, R>, value: Any?): UpdateBuilder<T> {
        nameValueMap[NameInformation.of(field)] = value
        return this
    }

    fun <R> set(field: KMutableProperty1<T, R>, value: Any?): UpdateBuilder<T> {
        nameValueMap[NameInformation.of(field)] = value
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

    fun build(recordId: String, persistentEntity: BitablePersistentEntity<*>): UpdateRequest {
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