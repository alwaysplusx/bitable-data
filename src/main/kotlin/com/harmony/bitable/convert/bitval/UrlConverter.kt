package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getPropertyValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord
import com.lark.oapi.service.bitable.v1.model.Url

/**
 * @author wuxin
 */
class UrlConverter : BitvalConverter {
    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.URL
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Url? {
        val value = record.getPropertyValue(property)
        return ValueConverters.convertObject(value, Url::class.java)
    }
}