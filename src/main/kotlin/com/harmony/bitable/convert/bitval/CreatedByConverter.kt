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
class CreatedByConverter : BitvalConverter {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.CREATED_BY
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Person? {
        if (record.createdBy != null) {
            return record.createdBy
        }
        val value = record.getPropertyValue(property)
        return ValueConverters.convertArray(value, Person::class.java)?.firstOrNull()
    }

}