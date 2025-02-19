package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import org.springframework.core.convert.support.DefaultConversionService

/**
 * @author wuxin
 */
class NumberConverter : BitvalConverter {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.NUMBER
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        val value = record.getPropertyValue(property) ?: return null
        return DefaultConversionService.getSharedInstance().convert(value, property.type)
    }

}