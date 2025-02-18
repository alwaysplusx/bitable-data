package com.harmony.bitable.repository.support

import com.harmony.bitable.mapping.BitablePersistentEntity
import org.springframework.data.repository.core.support.AbstractEntityInformation

/**
 * 数据实体与 bitable 的信息集
 */
class BitableEntityInformation<T : Any>(
    private val persistentEntity: BitablePersistentEntity<T>,
) : AbstractEntityInformation<T, String>(persistentEntity.type) {

    override fun getId(entity: T): String? {
        return persistentEntity.getRecordIdAccessor(entity).getRecordId()
    }

    override fun getIdType(): Class<String> = String::class.java

}
