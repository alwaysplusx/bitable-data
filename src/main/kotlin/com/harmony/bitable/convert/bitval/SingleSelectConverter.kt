package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.core.Option
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.convert.bitval.ValueConverters.isOptionEnum
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class SingleSelectConverter : BitvalConverter {

    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.SINGLE_SELECT
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property) ?: return null
        return when {
            isOptionEnum(property.typeInformation) -> ValueConverters.convertToOption(value, property.type)
            else -> value.toString()
        }
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        record.fields[property.getBitfieldName()] = when (value) {
            is Option -> value.getValue()
            else -> value?.toString()
        }
    }

}