package com.harmony.bitable.convert.bitval.impl

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.bitval.BitvalReader
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Person

/**
 * @author wuxin
 */
class PersonBitvalReader : BitvalReader {
    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.PERSON
    }

    override fun read(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property) ?: return null
        val persons = ValueConverters.convertArray(value, Person::class.java) ?: return null
        return when {
            property.type.isArray -> persons
            property.type == List::class.java -> persons.toList()
            property.type == Person::class.java -> persons.firstOrNull()
            else -> {
                throw IllegalArgumentException("Unsupported Person type: ${property.type}")
            }
        }
    }
}