package com.harmony.bitable.core

import com.harmony.bitable.mapping.BitablePersistentEntity
import org.springframework.data.repository.core.support.AbstractEntityInformation

/**
 * 数据实体与 bitable 的信息集
 */
class BitableEntityInformation<T : Any, ID>(
    private val persistentEntity: BitablePersistentEntity<T>,
) : AbstractEntityInformation<T, ID>(persistentEntity.type) {

    override fun getId(entity: T): ID? = persistentEntity.getIdentifierAccessor(entity).identifier as ID

    override fun getIdType(): Class<ID> = persistentEntity.requiredIdProperty.type as Class<ID>

}
