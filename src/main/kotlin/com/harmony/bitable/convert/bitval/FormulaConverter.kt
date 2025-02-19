package com.harmony.bitable.convert.bitval

import com.harmony.bitable.BitfieldType
import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class FormulaConverter : BitvalConverter {
    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.getBitfieldType() == BitfieldType.FORMULA
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        // TODO Not yet implemented
        return null
    }
}