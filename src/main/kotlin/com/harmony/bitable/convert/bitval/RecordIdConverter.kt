package com.harmony.bitable.convert.bitval

import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class RecordIdConverter : BitvalConverter {

    override fun canRead(property: BitablePersistentProperty): Boolean {
        return property.isRecordIdProperty()
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        return record.recordId
    }

}