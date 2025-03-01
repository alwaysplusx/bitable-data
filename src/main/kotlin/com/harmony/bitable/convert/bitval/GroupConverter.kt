package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Group

/**
 * @author wuxin
 */
class GroupConverter : BitvalConverter {
    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.GROUP || property.type.isAssignableFrom(Group::class.java)
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property)
        val groups = ValueConverters.convertToArray(value, Group::class.java) ?: return null
        return when {
            property.type.isArray -> groups
            property.type == List::class.java -> groups.toList()
            property.type == Group::class.java -> groups.firstOrNull()
            else -> {
                throw IllegalArgumentException("Unsupported Group type: ${property.type}")
            }
        }
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        record.fields[property.getBitfieldName()] = when (value) {
            is List<*> -> value
            is Group -> arrayOf(value)
            is Array<*> -> value
            else -> {
                throw IllegalArgumentException("Unsupported Group value: $value")
            }
        }
    }

}