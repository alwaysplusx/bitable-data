package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.convert.bitval.ValueConverters.convertToArray
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class LookupConverter : BitvalConverter {

    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.LOOKUP
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value: Any = lookupValue(property, record) ?: return null
        if (value !is List<*> || value.isEmpty()) {
            return null
        }
        return when {
            property.type.isArray -> convertToArray(value, property.rawType)
            property.type == List::class.java -> convertToArray(value, property.rawType)?.toList()
            else -> {
                return ValueConverters.convertToObject(value.first(), property.type)
            }
        }
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        throw UnsupportedOperationException("lookup value not writeable")
    }

    private fun lookupValue(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val propertyValue = record.getPropertyValue(property) ?: return null
        return (propertyValue as Map<*, *>)["value"]
    }

}