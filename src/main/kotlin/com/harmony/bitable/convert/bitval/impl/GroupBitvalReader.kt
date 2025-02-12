package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Attachment
import com.lark.oapi.service.bitable.v1.model.Group

/**
 * @author wuxin
 */
class GroupBitvalReader : BitvalReader {
    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.GROUP || property.type.isAssignableFrom(Group::class.java)
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property)
        val groups = ValueConverters.convertArray(value, Group::class.java) ?: return null
        return when {
            property.type.isArray -> groups
            property.type == List::class.java -> groups.toList()
            property.type == Attachment::class.java -> groups.firstOrNull()
            else -> {
                throw IllegalArgumentException("Unsupported Group type: ${property.type}")
            }
        }
    }

}