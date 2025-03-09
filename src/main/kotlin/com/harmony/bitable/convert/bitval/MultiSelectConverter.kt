package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.core.Option
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.data.util.TypeInformation

/**
 * @author wuxin
 */
class MultiSelectConverter : BitvalConverter {

    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.MULTI_SELECT
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property) ?: return null
        val result = ValueConverters.convertToArray(value, String::class.java) ?: return null

        val rowTypeInformation = property.typeInformation.rawTypeInformation
        val converter = toOptionConverter(rowTypeInformation)
        return when {
            property.type.isArray -> result.map(converter)
            property.type == List::class.java -> result.map(converter).toList()
            else -> {
                throw IllegalArgumentException("Unsupported MultiSelect type: ${property.type}")
            }
        }
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        if (value == null) {
            return
        }
        val rowTypeInformation = property.typeInformation.rawTypeInformation
        val converter = fromOptionConverter(rowTypeInformation)
        record.fields[property.getBitfieldName()] = when (value) {
            is Array<*> -> value.map(converter)
            is List<*> -> value.map(converter)
            else -> {
                throw IllegalArgumentException("Unsupported MultiSelect type: ${property.type}")
            }
        }
    }

    private fun toOptionConverter(type: TypeInformation<*>): (String?) -> Any? {
        return if (ValueConverters.isOptionEnum(type)) {
            { s: String? -> ValueConverters.convertToOption(s, type.type) }
        } else {
            { s: String? -> s }
        }
    }

    private fun fromOptionConverter(type: TypeInformation<*>): (Any?) -> String? {
        return if (ValueConverters.isOptionEnum(type)) {
            { s: Any? -> if (s == null) null else (s as Option).getValue() }
        } else {
            { s: Any? -> s?.toString() }
        }
    }

}