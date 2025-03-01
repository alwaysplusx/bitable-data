package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Person

/**
 * @author wuxin
 */
class PersonConverter : BitvalConverter {
    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.PERSON
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property) ?: return null
        val persons = ValueConverters.convertToArray(value, Person::class.java) ?: return null
        return when {
            property.type.isArray -> persons
            property.type == List::class.java -> persons.toList()
            property.type == Person::class.java -> persons.firstOrNull()
            else -> {
                throw IllegalArgumentException("Unsupported Person type: ${property.type}")
            }
        }
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        if (value == null) {
            return
        }
        record.fields[property.getBitfieldName()] = when (value) {
            is List<*> -> value
            is Person -> arrayOf(value)
            is Array<*> -> value
            else -> {
                throw IllegalArgumentException("Unsupported Person value: $value")
            }
        }
    }

}