package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.harmony.bitable.oapi.getFieldValue
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class SingleSelectConverter : BitvalConverter {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.SINGLE_SELECT
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        // TODO or enum
        return record.getFieldValue(property.getBitfieldName())
    }

}