package com.harmony.bitable.convert.bitval

import com.harmony.bitable.convert.BitvalConverter
import com.harmony.bitable.mapping.BitablePersistentProperty
import com.lark.oapi.service.bitable.v1.model.AppTableRecord

/**
 * @author wuxin
 */
class RecordIdConverter : BitvalConverter {

    override fun canHandle(property: BitablePersistentProperty): Boolean {
        return property.isRecordId()
    }

    override fun readAndConvert(property: BitablePersistentProperty, record: AppTableRecord): Any? {
        return record.recordId
    }

    override fun convertAndWrite(value: Any?, property: BitablePersistentProperty, record: AppTableRecord) {
        record.recordId = value?.toString()
    }

}